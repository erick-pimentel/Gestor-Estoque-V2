(function (window, document) {
  const app = window.GestorEstoque;
  const ui = window.GestorEstoqueUI;

  const state = app.state;
  const form = document.getElementById('produtoForm');
  const tableBody = document.getElementById('produtosBody');
  const feedback = document.getElementById('produtoFeedback');
  const submitButton = document.getElementById('produtoSubmitBtn');
  const cancelButton = document.getElementById('produtoCancelBtn');
  const hiddenId = document.getElementById('produtoId');
  const supplierSelect = document.getElementById('fornecedor');

  let editingId = null;

  function populateSuppliers() {
    if (!supplierSelect) {
      return;
    }

    const options = ['<option value="">Selecione um fornecedor</option>'];
    state.fornecedores.forEach((supplier) => {
      options.push(`<option value="${supplier.id}">${supplier.nome}</option>`);
    });

    supplierSelect.innerHTML = options.join('');
  }

  function getProductPayload() {
    return {
      nome: document.getElementById('nome').value.trim(),
      codigo: document.getElementById('codigo').value.trim(),
      quantidade: Number(document.getElementById('quantidade').value),
      preco: ui.moneyToNumber(document.getElementById('preco').value),
      valorVenda: ui.moneyToNumber(document.getElementById('valorVenda').value),
      validade: document.getElementById('validade').value,
      fornecedorId: supplierSelect.value,
    };
  }

  function validateProduct(payload) {
    if (!payload.nome || !payload.codigo || !payload.fornecedorId) {
      return 'Preencha nome, codigo e fornecedor.';
    }

    if (!Number.isFinite(payload.quantidade) || payload.quantidade <= 0) {
      return 'Quantidade deve ser maior que zero.';
    }

    if (!Number.isFinite(payload.preco) || payload.preco <= 0) {
      return 'Preco deve ser maior que zero.';
    }

    const duplicateCode = state.produtos.find((produto) => produto.codigo === payload.codigo && produto.id !== editingId);
    if (duplicateCode) {
      return 'Ja existe um produto cadastrado com este codigo.';
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

  function startEdit(productId) {
    const product = app.findProductById(productId);
    if (!product) {
      return;
    }

    editingId = product.id;
    hiddenId.value = product.id;
    document.getElementById('nome').value = product.nome;
    document.getElementById('codigo').value = product.codigo;
    document.getElementById('quantidade').value = product.quantidade;
    document.getElementById('preco').value = product.preco;
    document.getElementById('valorVenda').value = product.valorVenda || '';
    document.getElementById('validade').value = product.validade || '';
    supplierSelect.value = product.fornecedorId || '';
    submitButton.textContent = 'Salvar edição';
    cancelButton.hidden = false;
  }

  function removeProduct(productId) {
    const index = state.produtos.findIndex((produto) => produto.id === productId);
    if (index === -1) {
      return;
    }

    state.produtos.splice(index, 1);
    state.movimentacoes.forEach((movement) => {
      if (movement.produtoId === productId) {
        movement.produtoNome = movement.produtoNome || 'Produto removido';
      }
    });
    app.persistState();
    renderTable();
    ui.showFeedback(feedback, 'Produto excluido!', 'success');

    if (editingId === productId) {
      resetForm();
    }
  }

  function renderTable() {
    if (!tableBody) {
      return;
    }

    const rows = state.produtos
      .slice()
      .sort((left, right) => left.nome.localeCompare(right.nome))
      .map((product) => {
        const supplier = app.findSupplierById(product.fornecedorId);
        return `
          <tr data-product-id="${product.id}">
            <td>${product.codigo}</td>
            <td>${product.nome}</td>
            <td>${product.quantidade}</td>
            <td>${app.formatMoney(product.preco)}</td>
            <td>${supplier ? supplier.nome : '-'}</td>
            <td>${app.formatDate(product.validade)}</td>
            <td>
              <div class="table-actions">
                <button type="button" class="btn-outline-custom table-action-btn" data-action="edit" data-id="${product.id}">Editar</button>
                <button type="button" class="btn-outline-custom table-action-btn" data-action="delete" data-id="${product.id}">Excluir</button>
              </div>
            </td>
          </tr>
        `;
      })
      .join('');

    tableBody.innerHTML = rows || '<tr><td colspan="7">Nenhum produto cadastrado.</td></tr>';
  }

  function handleTableClick(event) {
    const row = event.target.closest('tr[data-product-id]');
    const actionButton = event.target.closest('button[data-action]');

    if (actionButton) {
      const productId = actionButton.dataset.id;
      if (actionButton.dataset.action === 'edit') {
        startEdit(productId);
        return;
      }

      if (actionButton.dataset.action === 'delete') {
        ui.showModal({
          title: 'Excluir produto',
          message: 'Deseja excluir este produto?',
          confirmText: 'Excluir',
          onConfirm: () => removeProduct(productId),
        });
        return;
      }
    }

    if (row) {
      startEdit(row.dataset.productId);
    }
  }

  function handleSubmit(event) {
    event.preventDefault();

    const payload = getProductPayload();
    const validationMessage = validateProduct(payload);
    if (validationMessage) {
      ui.showFeedback(feedback, validationMessage, 'error');
      return;
    }

    if (editingId) {
      const product = app.findProductById(editingId);
      if (!product) {
        ui.showFeedback(feedback, 'Produto nao encontrado.', 'error');
        return;
      }

      product.nome = payload.nome;
      product.codigo = payload.codigo;
      product.quantidade = payload.quantidade;
      product.preco = payload.preco;
      product.valorVenda = payload.valorVenda;
      product.validade = payload.validade;
      product.fornecedorId = payload.fornecedorId;
      app.persistState();
      renderTable();
      ui.showFeedback(feedback, 'Produto atualizado!', 'success');
      resetForm();
      return;
    }

    state.produtos.push({
      id: app.generateId('prod'),
      nome: payload.nome,
      codigo: payload.codigo,
      quantidade: payload.quantidade,
      preco: payload.preco,
      valorVenda: payload.valorVenda,
      validade: payload.validade,
      fornecedorId: payload.fornecedorId,
    });

    app.persistState();
    renderTable();
    ui.showFeedback(feedback, 'Produto cadastrado!', 'success');
    resetForm();
  }

  function init() {
    if (!ui.requireSession() || !ui.requirePageAccess()) {
      return;
    }

    ui.setActiveNav();
    ui.bindLogoutButtons();
    ui.renderTransientNotice();
    populateSuppliers();
    renderTable();

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

    if (supplierSelect) {
      supplierSelect.addEventListener('focus', populateSuppliers);
    }
  }

  document.addEventListener('DOMContentLoaded', init);
})(window, document);