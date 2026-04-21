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
  const SUPPLIER_API_URL = '/api/fornecedores';
  const PRODUCT_API_URL = '/api/produtos';

  let editingId = null;

  function normalizeProductCode(value) {
    return String(value || '').trim().toUpperCase();
  }

  function extractApiErrorMessage(payload, fallback) {
    if (payload && typeof payload === 'object') {
      if (payload.message && String(payload.message).trim()) {
        return String(payload.message).trim();
      }

      if (payload.fields && typeof payload.fields === 'object') {
        const firstField = Object.keys(payload.fields)[0];
        if (firstField && payload.fields[firstField]) {
          return String(payload.fields[firstField]);
        }
      }
    }

    if (typeof payload === 'string' && payload.trim()) {
      return payload.trim();
    }

    return fallback;
  }

  async function readApiError(response, fallback) {
    try {
      const text = await response.text();
      if (!text) {
        return fallback;
      }

      try {
        return extractApiErrorMessage(JSON.parse(text), fallback);
      } catch (parseError) {
        return extractApiErrorMessage(text, fallback);
      }
    } catch (error) {
      return fallback;
    }
  }

  function normalizeSupplierId(value) {
    const raw = String(value || '').trim();
    const digits = raw.replace(/\D/g, '');
    const parsed = Number(digits || raw);
    return Number.isFinite(parsed) && parsed > 0 ? parsed : null;
  }

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

  function normalizeSupplierFromApi(apiSupplier) {
    return {
      id: Number(apiSupplier && (apiSupplier.id ?? apiSupplier.idFornecedor)),
      nome: (apiSupplier && (apiSupplier.nome ?? apiSupplier.nomeFornecedor)) || '',
      cnpj: (apiSupplier && (apiSupplier.cnpj ?? apiSupplier.cnpjFornecedor)) || '',
      contato: (apiSupplier && (apiSupplier.contato ?? apiSupplier.contatoFornecedor)) || '',
      email: (apiSupplier && (apiSupplier.email ?? apiSupplier.emailFornecedor)) || '',
    };
  }

  function normalizeProductFromApi(apiProduct) {
    return {
      id: Number(apiProduct && apiProduct.id),
      nome: (apiProduct && apiProduct.nome) || '',
      codigo: (apiProduct && apiProduct.codigo) || '',
      quantidade: Number(apiProduct && apiProduct.quantidade) || 0,
      precoCusto: Number(apiProduct && apiProduct.precoCusto) || 0,
      precoVenda: Number(apiProduct && apiProduct.precoVenda) || 0,
      validade: (apiProduct && apiProduct.validade) || '',
      idFornecedor: normalizeSupplierId(apiProduct && apiProduct.idFornecedor),
      fornecedorId: String((apiProduct && apiProduct.idFornecedor) || ''),
    };
  }

  async function loadSuppliersFromApi() {
    const response = await window.fetch(SUPPLIER_API_URL, {
      method: 'GET',
      headers: {
        ...ui.getAuthHeaders(),
      },
    });

    if (!response.ok) {
      throw new Error('Nao foi possivel carregar fornecedores cadastrados no banco.');
    }

    const payload = await response.json();
    state.fornecedores = Array.isArray(payload)
      ? payload.map(normalizeSupplierFromApi).filter((supplier) => Number.isFinite(supplier.id) && supplier.id > 0)
      : [];
    app.persistState();
  }

  async function loadProductsFromApi() {
    const response = await window.fetch(PRODUCT_API_URL, {
      method: 'GET',
      headers: {
        ...ui.getAuthHeaders(),
      },
    });

    if (!response.ok) {
      throw new Error('Nao foi possivel carregar produtos cadastrados no banco.');
    }

    const payload = await response.json();
    state.produtos = Array.isArray(payload)
      ? payload.map(normalizeProductFromApi).filter((product) => Number.isFinite(product.id) && product.id > 0)
      : [];
    app.persistState();
  }

  function getProductPayload() {
    const nome = document.getElementById('nome').value.replace(/\s+/g, ' ').trim();
    const codigo = normalizeProductCode(document.getElementById('codigo').value);
    const quantidade = Number(document.getElementById('quantidade').value);
    const precoCusto = ui.moneyToNumber(document.getElementById('preco').value);
    const precoVenda = ui.moneyToNumber(document.getElementById('valorVenda').value);

    return {
      nome,
      codigo,
      quantidade,
      precoCusto,
      precoVenda,
      validade: document.getElementById('validade').value,
      idFornecedor: normalizeSupplierId(supplierSelect.value),
    };
  }

  function validateProduct(payload) {
    if (!payload.nome || !payload.codigo || !payload.idFornecedor) {
      return 'Preencha nome, codigo e fornecedor.';
    }

    if (payload.nome.length < 2 || payload.nome.length > 120) {
      return 'Nome do produto deve ter entre 2 e 120 caracteres.';
    }

    if (!/^[A-Z0-9._-]{1,40}$/.test(payload.codigo)) {
      return 'Codigo invalido. Use letras, numeros, ponto, traco ou underline.';
    }

    if (!Number.isInteger(payload.quantidade) || payload.quantidade < 0) {
      return 'Quantidade deve ser um numero inteiro maior ou igual a zero.';
    }

    if (!Number.isFinite(payload.precoCusto) || payload.precoCusto <= 0) {
      return 'Preco de compra deve ser maior que zero.';
    }

    if (!Number.isFinite(payload.precoVenda) || payload.precoVenda <= 0) {
      return 'Preco de venda deve ser maior que zero.';
    }

    if (payload.precoVenda < payload.precoCusto) {
      return 'Preco de venda nao pode ser menor que o preco de compra.';
    }

    if (payload.validade) {
      const validade = new Date(payload.validade);
      if (Number.isNaN(validade.getTime())) {
        return 'Data de validade invalida.';
      }
    }

    const duplicateCode = state.produtos.find((produto) => String(produto.codigo || '').toUpperCase() === payload.codigo && produto.id !== editingId);
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
    document.getElementById('preco').value = product.precoCusto ?? product.precoCompra ?? product.preco ?? '';
    document.getElementById('valorVenda').value = product.precoVenda ?? product.valorVenda ?? '';
    document.getElementById('validade').value = product.validade || '';
    supplierSelect.value = product.idFornecedor || product.fornecedorId || '';
    submitButton.textContent = 'Salvar edicao';
    cancelButton.hidden = false;
  }

  async function removeProduct(productId) {
    const id = normalizeSupplierId(productId);
    if (!id) {
      ui.showFeedback(feedback, 'Produto invalido.', 'error');
      return;
    }

    const response = await window.fetch(`${PRODUCT_API_URL}/${id}`, {
      method: 'DELETE',
      headers: {
        ...ui.getAuthHeaders(),
      },
    });

    if (!response.ok) {
      throw new Error('Nao foi possivel excluir o produto.');
    }

    const index = state.produtos.findIndex((produto) => produto.id === id);
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

    if (editingId === id) {
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
        const supplier = app.findSupplierById(product.idFornecedor || product.fornecedorId);
        return `
          <tr data-product-id="${product.id}">
            <td>${product.codigo}</td>
            <td>${product.nome}</td>
            <td>${product.quantidade}</td>
            <td>${app.formatMoney(product.precoCusto ?? product.precoCompra ?? product.preco)}</td>
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
          onConfirm: async () => {
            try {
              await removeProduct(productId);
            } catch (error) {
              ui.showFeedback(feedback, error.message || 'Nao foi possivel excluir o produto.', 'error');
            }
          },
        });
        return;
      }
    }

    if (row) {
      startEdit(row.dataset.productId);
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const session = app.getSession();
    if (!session || !session.token) {
      ui.showFeedback(feedback, 'Sessao invalida. Faca login novamente.', 'error');
      return;
    }

    const payload = getProductPayload();
    const validationMessage = validateProduct(payload);
    if (validationMessage) {
      ui.showFeedback(feedback, validationMessage, 'error');
      return;
    }

    try {
      if (editingId) {
        const response = await window.fetch(`${PRODUCT_API_URL}/${editingId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            ...ui.getAuthHeaders(),
          },
          body: JSON.stringify(payload),
        });

        if (!response.ok) {
          throw new Error(await readApiError(response, 'Nao foi possivel atualizar o produto.'));
        }

        const updatedProduct = normalizeProductFromApi(await response.json());
        const index = state.produtos.findIndex((produto) => produto.id === updatedProduct.id);
        if (index >= 0) {
          state.produtos[index] = updatedProduct;
        }

        app.persistState();
        renderTable();
        ui.showFeedback(feedback, 'Produto atualizado!', 'success');
        resetForm();
        return;
      }

      const response = await window.fetch(PRODUCT_API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...ui.getAuthHeaders(),
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error(await readApiError(response, 'Nao foi possivel cadastrar o produto.'));
      }

      const createdProduct = normalizeProductFromApi(await response.json());
      state.produtos.push(createdProduct);
      app.persistState();
      renderTable();
      ui.showFeedback(feedback, 'Produto cadastrado!', 'success');
      resetForm();
    } catch (error) {
      ui.showFeedback(feedback, error.message || 'Nao foi possivel salvar o produto.', 'error');
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
      await Promise.all([loadSuppliersFromApi(), loadProductsFromApi()]);
    } catch (error) {
      ui.showFeedback(feedback, error.message || 'Nao foi possivel carregar dados de produtos.', 'error');
    }
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


