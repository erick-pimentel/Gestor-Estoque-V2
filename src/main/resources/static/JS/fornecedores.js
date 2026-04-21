(function (window, document) {
  const app = window.GestorEstoque;
  const ui = window.GestorEstoqueUI;

  const state = app.state;
  const form = document.getElementById('fornecedorForm');
  const tableBody = document.getElementById('fornecedoresBody');
  const feedback = document.getElementById('fornecedorFeedback');
  const submitButton = document.getElementById('fornecedorSubmitBtn');
  const cancelButton = document.getElementById('fornecedorCancelBtn');
  const hiddenId = document.getElementById('fornecedorId');
  const cnpjInput = document.getElementById('cnpj');
  const API_URL = '/api/fornecedores';

  let editingId = null;

  function normalizeId(value) {
    const parsed = Number(value);
    return Number.isFinite(parsed) && parsed > 0 ? parsed : null;
  }

  function normalizeCnpj(value) {
    return String(value || '').replace(/\s+/g, '').trim();
  }

  function toApiCreatePayload(payload) {
    return {
      nome: payload.nome,
      cnpj: payload.cnpj,
      contato: payload.contato,
      email: payload.email,
    };
  }

  function toApiUpdatePayload(payload) {
    return {
      nome: payload.nome,
      cnpj: payload.cnpj,
      contato: payload.contato,
      email: payload.email,
    };
  }

  function fromApiSupplier(apiSupplier) {
    return {
      id: normalizeId(apiSupplier && (apiSupplier.id ?? apiSupplier.idFornecedor)),
      nome: (apiSupplier && (apiSupplier.nome ?? apiSupplier.nomeFornecedor)) || '',
      cnpj: (apiSupplier && (apiSupplier.cnpj ?? apiSupplier.cnpjFornecedor)) || '',
      contato: (apiSupplier && (apiSupplier.contato ?? apiSupplier.contatoFornecedor)) || '',
      email: (apiSupplier && (apiSupplier.email ?? apiSupplier.emailFornecedor)) || '',
    };
  }

  async function apiRequest(url, options) {
    const response = await window.fetch(url, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...ui.getAuthHeaders(),
        ...(options && options.headers ? options.headers : {}),
      },
    });

    if (response.status === 401 || response.status === 403) {
      throw new Error('Sessao invalida. Faca login novamente.');
    }

    if (!response.ok) {
      let message = 'Nao foi possivel salvar o fornecedor.';
      try {
        const error = await response.json();
        const apiMessage = error && (error.message || error.mensagem || error.detail || error.title);
        if (typeof apiMessage === 'string' && apiMessage.trim()) {
          message = apiMessage;
        }
      } catch (ignored) {
        try {
          const rawText = await response.text();
          if (rawText && rawText.trim()) {
            message = rawText.trim();
          }
        } catch (ignoredText) {
          // Keep generic message when response body cannot be read.
        }
      }
      throw new Error(message);
    }

    if (response.status === 204) {
      return null;
    }

    return response.json();
  }

  async function loadSuppliers() {
    const response = await apiRequest(API_URL, { method: 'GET' });
    state.fornecedores = Array.isArray(response) ? response.map(fromApiSupplier) : [];
    app.persistState();
  }

  function getSupplierPayload() {
    const nome = document.getElementById('nomeFornecedor').value.replace(/\s+/g, ' ').trim();
    const cnpj = normalizeCnpj(cnpjInput.value);
    const contatoRaw = document.getElementById('contato').value.replace(/\s+/g, ' ').trim();
    const emailRaw = document.getElementById('email').value.trim().toLowerCase();

    return {
      nome,
      cnpj,
      contato: contatoRaw,
      email: emailRaw,
    };
  }

  function validateSupplier(payload) {
    if (!payload.nome || !payload.cnpj) {
      return 'Preencha nome e CNPJ.';
    }

    if (payload.nome.length < 2 || payload.nome.length > 120) {
      return 'Nome do fornecedor deve ter entre 2 e 120 caracteres.';
    }

    if (!ui.isValidCnpj(payload.cnpj)) {
      return 'CNPJ invalido. Use o formato 00.000.000/0000-00.';
    }

    if (payload.contato && payload.contato.length > 120) {
      return 'Contato deve ter no maximo 120 caracteres.';
    }

    if (payload.email && payload.email.length > 120) {
      return 'E-mail deve ter no maximo 120 caracteres.';
    }

    if (payload.email && !ui.isValidEmail(payload.email)) {
      return 'E-mail invalido.';
    }

    const duplicateCnpj = state.fornecedores.find((supplier) => normalizeCnpj(supplier.cnpj) === payload.cnpj && supplier.id !== editingId);
    if (duplicateCnpj) {
      return 'Ja existe um fornecedor cadastrado com este CNPJ.';
    }

    return '';
  }

  function resetForm() {
    form.reset();
    editingId = null;
    hiddenId.value = '';
    submitButton.textContent = 'Cadastrar';
    cancelButton.hidden = true;
  }

  function startEdit(supplierId) {
    const normalizedId = normalizeId(supplierId);
    const supplier = state.fornecedores.find((item) => item.id === normalizedId);
    if (!supplier) {
      return;
    }

    editingId = supplier.id;
    hiddenId.value = supplier.id;
    document.getElementById('nomeFornecedor').value = supplier.nome;
    cnpjInput.value = supplier.cnpj;
    document.getElementById('contato').value = supplier.contato;
    document.getElementById('email').value = supplier.email;
    submitButton.textContent = 'Salvar edição';
    cancelButton.hidden = false;
  }

  function removeSupplier(supplierId) {
    const normalizedId = normalizeId(supplierId);
    const index = state.fornecedores.findIndex((supplier) => supplier.id === normalizedId);
    if (index === -1) {
      return;
    }

    state.fornecedores.splice(index, 1);
    app.persistState();
    renderTable();
    populateSupplierSelects();
    ui.showFeedback(feedback, 'Fornecedor excluido!', 'success');

    if (editingId === normalizedId) {
      resetForm();
    }
  }

  async function removeSupplierFromApi(supplierId) {
    const normalizedId = normalizeId(supplierId);
    if (!normalizedId) {
      ui.showFeedback(feedback, 'Fornecedor invalido.', 'error');
      return;
    }

    try {
      await apiRequest(`${API_URL}/${normalizedId}`, { method: 'DELETE' });
      removeSupplier(normalizedId);
    } catch (error) {
      ui.showFeedback(feedback, error.message || 'Nao foi possivel excluir o fornecedor.', 'error');
    }
  }

  function renderTable() {
    if (!tableBody) {
      return;
    }

    const rows = state.fornecedores
      .slice()
      .sort((left, right) => left.nome.localeCompare(right.nome))
      .map((supplier) => `
        <tr data-supplier-id="${supplier.id}">
          <td>${supplier.nome}</td>
          <td>${supplier.cnpj}</td>
          <td>${supplier.contato}</td>
          <td>${supplier.email}</td>
          <td>
            <div class="table-actions">
              <button type="button" class="btn-outline-custom table-action-btn" data-action="edit" data-id="${supplier.id}">Editar</button>
              <button type="button" class="btn-outline-custom table-action-btn" data-action="delete" data-id="${supplier.id}">Excluir</button>
            </div>
          </td>
        </tr>
      `)
      .join('');

    tableBody.innerHTML = rows || '<tr><td colspan="5">Nenhum fornecedor cadastrado.</td></tr>';
  }

  function populateSupplierSelects() {
    const supplierSelects = document.querySelectorAll('select[data-supplier-list="true"]');
    supplierSelects.forEach((select) => {
      const currentValue = select.value;
      const options = ['<option value="">Selecione</option>'];
      state.fornecedores.forEach((supplier) => {
        options.push(`<option value="${supplier.id}">${supplier.nome}</option>`);
      });
      select.innerHTML = options.join('');
      select.value = currentValue;
    });
  }

  function handleTableClick(event) {
    const row = event.target.closest('tr[data-supplier-id]');
    const actionButton = event.target.closest('button[data-action]');

    if (actionButton) {
      const supplierId = actionButton.dataset.id;
      if (actionButton.dataset.action === 'edit') {
        startEdit(supplierId);
        return;
      }

      if (actionButton.dataset.action === 'delete') {
        ui.showModal({
          title: 'Excluir fornecedor',
          message: 'Deseja excluir este fornecedor?',
          confirmText: 'Excluir',
          onConfirm: () => removeSupplierFromApi(supplierId),
        });
        return;
      }
    }

    if (row) {
      startEdit(row.dataset.supplierId);
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const session = app.getSession();
    if (!session || !session.token) {
      ui.showFeedback(feedback, 'Sessao invalida. Faca login novamente.', 'error');
      return;
    }

    const payload = getSupplierPayload();
    const validationMessage = validateSupplier(payload);
    if (validationMessage) {
      ui.showFeedback(feedback, validationMessage, 'error');
      return;
    }

    try {
      if (editingId) {
        const updated = await apiRequest(`${API_URL}/${editingId}`, {
          method: 'PUT',
          body: JSON.stringify(toApiUpdatePayload(payload)),
        });

        const normalized = fromApiSupplier(updated);
        const index = state.fornecedores.findIndex((supplier) => supplier.id === normalized.id);
        if (index >= 0) {
          state.fornecedores[index] = normalized;
        }

        app.persistState();
        renderTable();
        populateSupplierSelects();
        ui.showFeedback(feedback, 'Fornecedor atualizado!', 'success');
        resetForm();
        return;
      }

      const created = await apiRequest(API_URL, {
        method: 'POST',
        body: JSON.stringify(toApiCreatePayload(payload)),
      });

      state.fornecedores.push(fromApiSupplier(created));
      app.persistState();
      renderTable();
      populateSupplierSelects();
      ui.showFeedback(feedback, 'Fornecedor cadastrado!', 'success');
      resetForm();
    } catch (error) {
      ui.showFeedback(feedback, error.message || 'Nao foi possivel salvar o fornecedor.', 'error');
    }
  }

  async function init() {
    if (!ui.requireSession() || !ui.requirePageAccess()) {
      return;
    }

    ui.setActiveNav();
    ui.bindLogoutButtons();
    ui.renderTransientNotice();
    try {
      await loadSuppliers();
    } catch (error) {
      ui.showFeedback(feedback, error.message || 'Nao foi possivel carregar fornecedores.', 'error');
    }
    renderTable();
    populateSupplierSelects();

    if (form) {
      form.addEventListener('submit', handleSubmit);
    }

    if (tableBody) {
      tableBody.addEventListener('click', handleTableClick);
    }

    if (cancelButton) {
      cancelButton.addEventListener('click', () => {
        resetForm();
        ui.showFeedback(feedback, 'Edicao cancelada.', 'info');
      });
    }

    if (cnpjInput) {
      cnpjInput.addEventListener('input', (event) => {
        event.target.value = ui.maskCnpj(event.target.value);
      });
    }
  }

  document.addEventListener('DOMContentLoaded', init);
})(window, document);