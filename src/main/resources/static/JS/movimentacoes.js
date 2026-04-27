(function (window, document) {
  const app = window.GestorEstoque;
  const ui = window.GestorEstoqueUI;

  const state = app.state;
  const form = document.getElementById('movimentacaoForm');
  const tableBody = document.getElementById('movimentacoesBody');
  const feedback = document.getElementById('movimentacaoFeedback');
  const tipoSelect = document.getElementById('tipoMovimentacao');
  const produtoSelect = document.getElementById('produtoId');
  const quantidadeInput = document.getElementById('quantidade');
  const valorUnitarioInput = document.getElementById('valorUnitario');
  const fornecedorField = document.getElementById('campoFornecedor');
  const fornecedorSelect = document.getElementById('fornecedorId');
  const observacaoField = document.getElementById('campoObservacao');
  const observacaoInput = document.getElementById('observacao');

  const API_URL = '/api/movimentacoes';
  const PRODUTOS_API_URL = '/api/produtos';
  const FORNECEDORES_API_URL = '/api/fornecedores';

  function normalizeMovementFromApi(apiMovement) {
    return {
      id: Number(apiMovement && apiMovement.id),
      produtoId: Number(apiMovement && apiMovement.idProduto),
      produtoNome: (apiMovement && apiMovement.nomeProduto) || '',
      fornecedorId: apiMovement && apiMovement.idFornecedor ? Number(apiMovement.idFornecedor) : null,
      fornecedorNome: (apiMovement && apiMovement.nomeFornecedor) || '',
      tipo: (apiMovement && apiMovement.tipo) || '',
      quantidade: Number(apiMovement && apiMovement.quantidade) || 0,
      valor: Number(apiMovement && apiMovement.valorUnitario) || 0,
      data: (apiMovement && apiMovement.dataMovimentacao) || '',
      observacao: (apiMovement && apiMovement.observacao) || '',
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
      idFornecedor: Number(apiProduct && apiProduct.idFornecedor) || null,
    };
  }

  function normalizeSupplierFromApi(apiSupplier) {
    return {
      id: Number(apiSupplier && apiSupplier.id),
      nome: (apiSupplier && apiSupplier.nome) || '',
      cnpj: (apiSupplier && apiSupplier.cnpj) || '',
      contato: (apiSupplier && apiSupplier.contato) || '',
      email: (apiSupplier && apiSupplier.email) || '',
    };
  }

  async function readApiError(response, fallback) {
    try {
      const text = await response.text();
      if (!text) {
        return fallback;
      }

      try {
        const json = JSON.parse(text);
        if (json && typeof json === 'object') {
          if (json.message && String(json.message).trim()) {
            return String(json.message).trim();
          }
          if (json.fields && typeof json.fields === 'object') {
            const firstField = Object.keys(json.fields)[0];
            if (firstField && json.fields[firstField]) {
              return String(json.fields[firstField]);
            }
          }
        }
        return fallback;
      } catch (parseError) {
        return fallback;
      }
    } catch (error) {
      return fallback;
    }
  }

  async function loadProductsFromApi() {
    const response = await window.fetch(PRODUTOS_API_URL, {
      method: 'GET',
      headers: {
        ...ui.getAuthHeaders(),
      },
    });

    if (!response.ok) {
      throw new Error('Nao foi possivel carregar produtos cadastrados.');
    }

    const payload = await response.json();
    state.produtos = Array.isArray(payload)
      ? payload.map(normalizeProductFromApi).filter((product) => Number.isFinite(product.id) && product.id > 0)
      : [];
    app.persistState();
  }

  async function loadSuppliersFromApi() {
    const response = await window.fetch(FORNECEDORES_API_URL, {
      method: 'GET',
      headers: {
        ...ui.getAuthHeaders(),
      },
    });

    if (!response.ok) {
      throw new Error('Nao foi possivel carregar fornecedores cadastrados.');
    }

    const payload = await response.json();
    state.fornecedores = Array.isArray(payload)
      ? payload.map(normalizeSupplierFromApi).filter((supplier) => Number.isFinite(supplier.id) && supplier.id > 0)
      : [];
    app.persistState();
  }

  async function loadMovementsFromApi() {
    const response = await window.fetch(API_URL, {
      method: 'GET',
      headers: {
        ...ui.getAuthHeaders(),
      },
    });

    if (!response.ok) {
      throw new Error('Nao foi possivel carregar movimentacoes.');
    }

    const payload = await response.json();
    state.movimentacoes = Array.isArray(payload)
      ? payload.map(normalizeMovementFromApi).filter((movement) => Number.isFinite(movement.id) && movement.id > 0)
      : [];
    app.persistState();
  }

  function populateProducts() {
    if (!produtoSelect) {
      return;
    }

    const options = ['<option value="">Selecione um produto</option>'];
    state.produtos.forEach((produto) => {
      options.push(`<option value="${produto.id}">${produto.nome} (${produto.quantidade} un.)</option>`);
    });

    produtoSelect.innerHTML = options.join('');
  }

  function populateSuppliers() {
    if (!fornecedorSelect) {
      return;
    }

    const options = ['<option value="">Selecione um fornecedor</option>'];
    state.fornecedores.forEach((supplier) => {
      options.push(`<option value="${supplier.id}">${supplier.nome}</option>`);
    });

    fornecedorSelect.innerHTML = options.join('');
  }

  function updateTypeFields() {
    const tipo = tipoSelect.value;
    const isEntrada = tipo === 'ENTRADA';
    const isAjuste = tipo === 'AJUSTE';

    if (fornecedorField) {
      fornecedorField.hidden = !isEntrada;
    }
    if (observacaoField) {
      observacaoField.hidden = !isAjuste;
    }

    if (!isEntrada && fornecedorSelect) {
      fornecedorSelect.value = '';
    }

    if (!isAjuste && observacaoInput) {
      observacaoInput.value = '';
    }
  }

  function getMovementPayload() {
    const quantidadeValue = quantidadeInput ? String(quantidadeInput.value).trim() : '0';
    const valorValue = valorUnitarioInput ? String(valorUnitarioInput.value).trim() : '';
    const idFornecedor = fornecedorSelect && fornecedorSelect.value ? parseInt(fornecedorSelect.value, 10) : null;
    const quantidade = parseInt(quantidadeValue, 10);
    const valor = valorValue ? parseFloat(valorValue) : null;

    return {
      idProduto: parseInt(produtoSelect.value, 10),
      tipo: tipoSelect.value,
      quantidade: Number.isFinite(quantidade) && quantidade > 0 ? quantidade : 0,
      idFornecedor: idFornecedor,
      observacao: observacaoInput ? String(observacaoInput.value).trim() : '',
      valorUnitario: Number.isFinite(valor) && valor > 0 ? valor : null,
    };
  }

  function validateMovement(payload, product) {
    if (!payload.idProduto || !payload.tipo) {
      return 'Selecione produto e tipo de movimentacao.';
    }

    if (!Number.isFinite(payload.quantidade) || payload.quantidade <= 0) {
      return 'Quantidade deve ser maior que zero.';
    }

    if (payload.tipo === 'ENTRADA' && !payload.idFornecedor) {
      return 'Selecione o fornecedor da entrada.';
    }

    if (payload.tipo === 'AJUSTE' && !payload.observacao) {
      return 'Informe uma observacao para o ajuste.';
    }

    if (payload.tipo === 'SAIDA' && product && payload.quantidade > product.quantidade) {
      return 'Quantidade de saida nao pode ser maior que o saldo do produto.';
    }

    return '';
  }

  function renderTable() {
    if (!tableBody) {
      return;
    }

    const rows = state.movimentacoes
      .slice()
      .sort((left, right) => new Date(right.data) - new Date(left.data))
      .map((movement) => {
        const typeClass = movement.tipo === 'SAIDA' ? 'badge-low' : movement.tipo === 'AJUSTE' ? 'badge-warn' : 'badge-ok';
        const detail = movement.tipo === 'ENTRADA'
          ? movement.fornecedorNome || '-'
          : movement.tipo === 'AJUSTE'
            ? movement.observacao || '-'
            : '-';

        const quantidade = Number.isFinite(movement.quantidade) ? movement.quantidade : 0;
        const valor = movement.valor || 0;

        return `
          <tr>
            <td>${app.formatDateTime(movement.data)}</td>
            <td>${movement.produtoNome}</td>
            <td><span class="badge-status ${typeClass}">${movement.tipo}</span></td>
            <td>${quantidade}</td>
            <td>${app.formatMoney(valor)}</td>
            <td>${detail}</td>
          </tr>
        `;
      })
      .join('');

    tableBody.innerHTML = rows || '<tr><td colspan="6">Nenhuma movimentacao registrada.</td></tr>';
  }

  async function registerMovement(event) {
    event.preventDefault();

    const payload = getMovementPayload();
    const product = app.findProductById(payload.idProduto);
    const validationMessage = validateMovement(payload, product);
    if (validationMessage) {
      ui.showFeedback(feedback, validationMessage, 'error');
      return;
    }

    try {
      const response = await window.fetch(API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...ui.getAuthHeaders(),
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error(await readApiError(response, 'Nao foi possivel registrar a movimentacao.'));
      }

      const createdMovement = normalizeMovementFromApi(await response.json());
      state.movimentacoes.push(createdMovement);
      app.persistState();

      // Recarregar produtos para atualizar quantidades
      try {
        await loadProductsFromApi();
      } catch (error) {
        console.warn('Nao foi possivel recarregar produtos apos movimentacao:', error);
      }

      populateProducts();
      renderTable();
      form.reset();
      updateTypeFields();
      ui.showFeedback(feedback, 'Movimentacao registrada!', 'success');
    } catch (error) {
      ui.showFeedback(feedback, error.message || 'Nao foi possivel registrar a movimentacao.', 'error');
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
      await Promise.all([loadProductsFromApi(), loadSuppliersFromApi(), loadMovementsFromApi()]);
    } catch (error) {
      ui.showFeedback(feedback, error.message || 'Nao foi possivel carregar dados.', 'error');
    }

    populateProducts();
    populateSuppliers();
    renderTable();
    updateTypeFields();

    if (form) {
      form.addEventListener('submit', registerMovement);
    }

    if (tipoSelect) {
      tipoSelect.addEventListener('change', updateTypeFields);
    }
  }

  document.addEventListener('DOMContentLoaded', init);
})(window, document);