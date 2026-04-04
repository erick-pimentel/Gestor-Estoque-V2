(function (window) {
  const STORAGE_KEY = 'gestor-estoque-db-v1';
  const SESSION_KEY = 'gestor-estoque-session-v1';

  const defaultState = {
    usuarios: [
      {
        id: 'user-1',
        nome: 'Administrador',
        perfil: 'Admin',
        senha: '1234',
      },
      {
        id: 'user-2',
        nome: 'Operador Estoque',
        perfil: 'Operador',
        senha: '1234',
      },
    ],
    produtos: [
      {
        id: 'prod-1',
        nome: 'Arroz Integral 5kg',
        codigo: '001',
        quantidade: 48,
        preco: 24.9,
        valorVenda: 32.9,
        validade: '2026-09-30',
        fornecedorId: 'forn-1',
      },
      {
        id: 'prod-2',
        nome: 'Feijao Preto 1kg',
        codigo: '002',
        quantidade: 112,
        preco: 8.75,
        valorVenda: 12.4,
        validade: '2026-08-18',
        fornecedorId: 'forn-2',
      },
      {
        id: 'prod-3',
        nome: 'Acucar Refinado 1kg',
        codigo: '003',
        quantidade: 75,
        preco: 4.35,
        valorVenda: 6.8,
        validade: '2026-12-12',
        fornecedorId: 'forn-3',
      },
      {
        id: 'prod-4',
        nome: 'Cafe Torrado 500g',
        codigo: '004',
        quantidade: 36,
        preco: 14.2,
        valorVenda: 20.5,
        validade: '2026-11-01',
        fornecedorId: 'forn-1',
      },
    ],
    fornecedores: [
      {
        id: 'forn-1',
        nome: 'Alfa Distribuidora LTDA',
        cnpj: '12.345.678/0001-90',
        contato: 'Carla Souza',
        email: 'carla@alfa.com.br',
      },
      {
        id: 'forn-2',
        nome: 'Beta Alimentos S.A.',
        cnpj: '23.456.789/0001-12',
        contato: 'Rafael Lima',
        email: 'rafael@beta.com.br',
      },
      {
        id: 'forn-3',
        nome: 'Mercado Sul Atacado',
        cnpj: '34.567.890/0001-34',
        contato: 'Fernanda Alves',
        email: 'fernanda@mercadosul.com.br',
      },
    ],
    movimentacoes: [
      {
        id: 'mov-1',
        data: '2026-04-01T09:10:00.000Z',
        produtoId: 'prod-1',
        produtoNome: 'Arroz Integral 5kg',
        tipo: 'ENTRADA',
        quantidade: 20,
        valor: 498.0,
        fornecedorId: 'forn-1',
        fornecedorNome: 'Alfa Distribuidora LTDA',
      },
      {
        id: 'mov-2',
        data: '2026-04-02T13:40:00.000Z',
        produtoId: 'prod-2',
        produtoNome: 'Feijao Preto 1kg',
        tipo: 'SAIDA',
        quantidade: 15,
        valor: 131.25,
      },
      {
        id: 'mov-3',
        data: '2026-04-03T08:25:00.000Z',
        produtoId: 'prod-4',
        produtoNome: 'Cafe Torrado 500g',
        tipo: 'AJUSTE',
        quantidade: 4,
        valor: 56.8,
        observacao: 'Ajuste de inventario apos contagem',
      },
      {
        id: 'mov-4',
        data: '2026-04-03T16:15:00.000Z',
        produtoId: 'prod-3',
        produtoNome: 'Acucar Refinado 1kg',
        tipo: 'ENTRADA',
        quantidade: 30,
        valor: 130.5,
        fornecedorId: 'forn-3',
        fornecedorNome: 'Mercado Sul Atacado',
      },
      {
        id: 'mov-5',
        data: '2026-04-04T10:05:00.000Z',
        produtoId: 'prod-2',
        produtoNome: 'Feijao Preto 1kg',
        tipo: 'ENTRADA',
        quantidade: 18,
        valor: 157.5,
        fornecedorId: 'forn-2',
        fornecedorNome: 'Beta Alimentos S.A.',
      },
    ],
  };

  function clone(value) {
    return JSON.parse(JSON.stringify(value));
  }

  function loadState() {
    if (typeof window.localStorage === 'undefined') {
      return clone(defaultState);
    }

    const rawState = window.localStorage.getItem(STORAGE_KEY);
    if (!rawState) {
      return clone(defaultState);
    }

    try {
      const parsed = JSON.parse(rawState);
      return {
        usuarios: Array.isArray(parsed.usuarios) ? parsed.usuarios : clone(defaultState.usuarios),
        produtos: Array.isArray(parsed.produtos) ? parsed.produtos : clone(defaultState.produtos),
        fornecedores: Array.isArray(parsed.fornecedores) ? parsed.fornecedores : clone(defaultState.fornecedores),
        movimentacoes: Array.isArray(parsed.movimentacoes) ? parsed.movimentacoes : clone(defaultState.movimentacoes),
      };
    } catch (error) {
      return clone(defaultState);
    }
  }

  const state = loadState();

  function persistState() {
    if (typeof window.localStorage === 'undefined') {
      return;
    }

    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  }

  function resetState() {
    state.usuarios.splice(0, state.usuarios.length, ...clone(defaultState.usuarios));
    state.produtos.splice(0, state.produtos.length, ...clone(defaultState.produtos));
    state.fornecedores.splice(0, state.fornecedores.length, ...clone(defaultState.fornecedores));
    state.movimentacoes.splice(0, state.movimentacoes.length, ...clone(defaultState.movimentacoes));
    persistState();
  }

  function getSession() {
    if (typeof window.localStorage === 'undefined') {
      return null;
    }

    const rawSession = window.localStorage.getItem(SESSION_KEY);
    if (!rawSession) {
      return null;
    }

    try {
      return JSON.parse(rawSession);
    } catch (error) {
      return null;
    }
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

  function generateId(prefix) {
    if (window.crypto && typeof window.crypto.randomUUID === 'function') {
      return `${prefix}-${window.crypto.randomUUID()}`;
    }

    return `${prefix}-${Date.now()}-${Math.random().toString(16).slice(2, 8)}`;
  }

  function findProductById(id) {
    return state.produtos.find((produto) => produto.id === id) || null;
  }

  function findSupplierById(id) {
    return state.fornecedores.find((fornecedor) => fornecedor.id === id) || null;
  }

  function findUserById(id) {
    return state.usuarios.find((usuario) => usuario.id === id) || null;
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
    if (Number.isNaN(date.getTime())) {
      return '-';
    }

    return new Intl.DateTimeFormat('pt-BR').format(date);
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

  const usuario = {
    usuario: 'admin',
    senha: '1234',
    nome: 'Administrador',
  };

  window.GestorEstoque = {
    state,
    defaultState,
    usuario,
    STORAGE_KEY,
    SESSION_KEY,
    persistState,
    resetState,
    getSession,
    setSession,
    clearSession,
    generateId,
    findProductById,
    findSupplierById,
    findUserById,
    formatMoney,
    formatDate,
    formatDateTime,
    getWeekNumber,
  };

  window.usuarios = state.usuarios;
  window.produtos = state.produtos;
  window.fornecedores = state.fornecedores;
  window.movimentacoes = state.movimentacoes;
  window.usuario = usuario;

  persistState();
})(window);