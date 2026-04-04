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

  let editingId = null;

  function getSupplierPayload() {
    return {
      nome: document.getElementById('nomeFornecedor').value.trim(),
      cnpj: cnpjInput.value.trim(),
      contato: document.getElementById('contato').value.trim(),
      email: document.getElementById('email').value.trim(),
    };
  }

  function validateSupplier(payload) {
    if (!payload.nome || !payload.cnpj || !payload.contato || !payload.email) {
      return 'Preencha todos os campos obrigatorios.';
    }

    if (!ui.isValidCnpj(payload.cnpj)) {
      return 'CNPJ invalido. Use o formato 00.000.000/0000-00.';
    }

    if (!ui.isValidEmail(payload.email)) {
      return 'E-mail invalido.';
    }

    const duplicateCnpj = state.fornecedores.find((supplier) => supplier.cnpj === payload.cnpj && supplier.id !== editingId);
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
    const supplier = app.findSupplierById(supplierId);
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
    const index = state.fornecedores.findIndex((supplier) => supplier.id === supplierId);
    if (index === -1) {
      return;
    }

    state.fornecedores.splice(index, 1);
    app.persistState();
    renderTable();
    populateSupplierSelects();
    ui.showFeedback(feedback, 'Fornecedor excluido!', 'success');

    if (editingId === supplierId) {
      resetForm();
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
          onConfirm: () => removeSupplier(supplierId),
        });
        return;
      }
    }

    if (row) {
      startEdit(row.dataset.supplierId);
    }
  }

  function handleSubmit(event) {
    event.preventDefault();

    const payload = getSupplierPayload();
    const validationMessage = validateSupplier(payload);
    if (validationMessage) {
      ui.showFeedback(feedback, validationMessage, 'error');
      return;
    }

    if (editingId) {
      const supplier = app.findSupplierById(editingId);
      if (!supplier) {
        ui.showFeedback(feedback, 'Fornecedor nao encontrado.', 'error');
        return;
      }

      supplier.nome = payload.nome;
      supplier.cnpj = payload.cnpj;
      supplier.contato = payload.contato;
      supplier.email = payload.email;
      app.persistState();
      renderTable();
      populateSupplierSelects();
      ui.showFeedback(feedback, 'Fornecedor atualizado!', 'success');
      resetForm();
      return;
    }

    state.fornecedores.push({
      id: app.generateId('forn'),
      nome: payload.nome,
      cnpj: payload.cnpj,
      contato: payload.contato,
      email: payload.email,
    });

    app.persistState();
    renderTable();
    populateSupplierSelects();
    ui.showFeedback(feedback, 'Fornecedor cadastrado!', 'success');
    resetForm();
  }

  function init() {
    if (!ui.requireSession() || !ui.requirePageAccess()) {
      return;
    }

    ui.setActiveNav();
    ui.bindLogoutButtons();
    ui.renderTransientNotice();
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