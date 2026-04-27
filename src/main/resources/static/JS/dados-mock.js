(function (window) {
  const SESSION_KEY = 'gestor-estoque-session-v2';
  const STATE_KEY = 'gestor-estoque-state-v1';
  const API_BASE = '';

  class ApiError extends Error {
    constructor(status, message, payload) {
      super(message || 'Erro na requisicao');
      this.name = 'ApiError';
      this.status = status;
      this.payload = payload;
    }
  }

  const state = {
    usuarios: [],
    produtos: [],
    fornecedores: [],
    movimentacoes: [],
    relatorios: [],
  };

  function loadStateFromStorage() {
    if (typeof window.localStorage === 'undefined') {
      return;
    }

    const raw = window.localStorage.getItem(STATE_KEY);
    const saved = raw ? safeJsonParse(raw) : null;
    if (!saved || typeof saved !== 'object') {
      return;
    }

    Object.keys(state).forEach((key) => {
      if (Array.isArray(saved[key])) {
        state[key].splice(0, state[key].length, ...saved[key]);
      }
    });
  }

  function persistState() {
    if (typeof window.localStorage === 'undefined') {
      return;
    }
    window.localStorage.setItem(STATE_KEY, JSON.stringify(state));
  }

  function safeJsonParse(value) {
    try {
      return JSON.parse(value);
    } catch (error) {
      return null;
    }
  }

  function getSession() {
    if (typeof window.localStorage === 'undefined') {
      return null;
    }
    const raw = window.localStorage.getItem(SESSION_KEY);
    return raw ? safeJsonParse(raw) : null;
  }

  function setSession(session) {
    if (typeof window.localStorage === 'undefined') {
      return;
    }
    window.localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  }

  function clearSession() {
    if (typeof window.localStorage === 'undefined') {
      return;
    }
    window.localStorage.removeItem(SESSION_KEY);
  }

  function getToken() {
    const session = getSession();
    return session && session.token ? session.token : '';
  }

  function getErrorMessage(payload, fallback) {
    if (payload && typeof payload === 'object') {
      if (payload.message) {
        return payload.message;
      }
      if (payload.error) {
        return payload.error;
      }
    }
    if (typeof payload === 'string' && payload.trim()) {
      return payload;
    }
    return fallback || 'Erro inesperado';
  }

  async function request(path, options) {
    const config = options || {};
    const headers = new window.Headers(config.headers || {});
    const hasBody = typeof config.body !== 'undefined' && config.body !== null;

    if (hasBody && !headers.has('Content-Type')) {
      headers.set('Content-Type', 'application/json');
    }

    if (config.auth !== false) {
      const token = getToken();
      if (token) {
        headers.set('Authorization', `Bearer ${token}`);
      }
    }

    const response = await window.fetch(`${API_BASE}${path}`, {
      method: config.method || 'GET',
      headers,
      body: hasBody ? config.body : undefined,
    });

    const text = await response.text();
    const payload = text ? safeJsonParse(text) || text : null;

    if (!response.ok) {
      throw new ApiError(response.status, getErrorMessage(payload, `Falha HTTP ${response.status}`), payload);
    }

    return payload;
  }

  function setCollectionState(collectionName, values) {
    const target = state[collectionName];
    if (!Array.isArray(target)) {
      return;
    }
    target.splice(0, target.length, ...(Array.isArray(values) ? values : []));
  }

  function formatMoney(value) {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(Number(value) || 0);
  }

  function formatDate(value) {
    if (!value) {
      return '-';
    }
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? '-' : new Intl.DateTimeFormat('pt-BR').format(date);
  }

  function formatDateTime(value) {
    if (!value) {
      return '-';
    }
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return '-';
    }
    return new Intl.DateTimeFormat('pt-BR', {
      dateStyle: 'short',
      timeStyle: 'short',
    }).format(date);
  }

  function getWeekNumber(value) {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return '-';
    }
    const utcDate = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    const dayNumber = utcDate.getUTCDay() || 7;
    utcDate.setUTCDate(utcDate.getUTCDate() + 4 - dayNumber);
    const yearStart = new Date(Date.UTC(utcDate.getUTCFullYear(), 0, 1));
    const weekNumber = Math.ceil((((utcDate - yearStart) / 86400000) + 1) / 7);
    return String(weekNumber).padStart(2, '0');
  }

  function findProductById(id) {
    return state.produtos.find((item) => String(item.id) === String(id)) || null;
  }

  function findSupplierById(id) {
    return state.fornecedores.find((item) => String(item.id) === String(id)) || null;
  }

  function findUserById(id) {
    return state.usuarios.find((item) => String(item.id) === String(id)) || null;
  }

  function login(nomeUsuario, senhaUsuario) {
    return request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ nomeUsuario, senhaUsuario }),
      auth: false,
    });
  }

  function listProdutos() { return request('/api/produtos'); }
  function createProduto(payload) { return request('/api/produtos', { method: 'POST', body: JSON.stringify(payload) }); }
  function updateProduto(id, payload) { return request(`/api/produtos/${id}`, { method: 'PUT', body: JSON.stringify(payload) }); }
  function deleteProduto(id) { return request(`/api/produtos/${id}`, { method: 'DELETE' }); }

  function listFornecedores() { return request('/api/fornecedores'); }
  function createFornecedor(payload) { return request('/api/fornecedores', { method: 'POST', body: JSON.stringify(payload) }); }
  function updateFornecedor(id, payload) { return request(`/api/fornecedores/${id}`, { method: 'PUT', body: JSON.stringify(payload) }); }
  function deleteFornecedor(id) { return request(`/api/fornecedores/${id}`, { method: 'DELETE' }); }

  function listMovimentacoes() { return request('/api/movimentacoes'); }
  function createMovimentacao(payload) { return request('/api/movimentacoes', { method: 'POST', body: JSON.stringify(payload) }); }
  function deleteMovimentacao(id) { return request(`/api/movimentacoes/${id}`, { method: 'DELETE' }); }

  function listUsuarios() { return request('/api/usuarios'); }
  function createUsuario(payload) { return request('/api/usuarios', { method: 'POST', body: JSON.stringify(payload) }); }
  function updateUsuario(id, payload) { return request(`/api/usuarios/${id}`, { method: 'PUT', body: JSON.stringify(payload) }); }
  function deleteUsuario(id) { return request(`/api/usuarios/${id}`, { method: 'DELETE' }); }

  function getRelatorioMovimentacoes(dataInicio, dataFim) {
    const query = new URLSearchParams({ dataInicio, dataFim });
    return request(`/api/relatorios/movimentacoes?${query.toString()}`);
  }

  function getRelatorioEstoque() {
    return request('/api/relatorios/estoque');
  }

  function getRelatorioProdutos(dataInicio, dataFim) {
    const query = new URLSearchParams({ dataInicio, dataFim });
    return request(`/api/relatorios/produtos?${query.toString()}`);
  }

  function getRelatorioFornecedores(dataInicio, dataFim) {
    const query = new URLSearchParams({ dataInicio, dataFim });
    return request(`/api/relatorios/fornecedores?${query.toString()}`);
  }

  window.GestorEstoque = {
    state,
    SESSION_KEY,
    ApiError,
    getSession,
    setSession,
    clearSession,
    getToken,
    persistState,
    setCollectionState,
    getErrorMessage,
    login,
    listProdutos,
    createProduto,
    updateProduto,
    deleteProduto,
    listFornecedores,
    createFornecedor,
    updateFornecedor,
    deleteFornecedor,
    listMovimentacoes,
    createMovimentacao,
    deleteMovimentacao,
    listUsuarios,
    createUsuario,
    updateUsuario,
    deleteUsuario,
    getRelatorioMovimentacoes,
    getRelatorioEstoque,
    getRelatorioProdutos,
    getRelatorioFornecedores,
    findProductById,
    findSupplierById,
    findUserById,
    formatMoney,
    formatDate,
    formatDateTime,
    getWeekNumber,
  };

  loadStateFromStorage();
})(window);