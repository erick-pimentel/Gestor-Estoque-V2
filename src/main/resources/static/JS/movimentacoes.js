(function (window, document) {
  const app = window.GestorEstoque;
  const ui = window.GestorEstoqueUI;

  const state = app.state;
  const form = document.getElementById('movimentacaoForm');
  const tableBody = document.getElementById('movimentacoesBody');
  const feedback = document.getElementById('movimentacaoFeedback');
  const produtoSelect = document.getElementById('produtoId');
  const tipoSelect = document.getElementById('tipoMovimentacao');
  const fornecedorField = document.getElementById('campoFornecedor');
  const observacaoField = document.getElementById('campoObservacao');
  const fornecedorSelect = document.getElementById('fornecedorId');
  const observacaoInput = document.getElementById('observacao');

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

    ui.showElement(fornecedorField);
    ui.showElement(observacaoField);

    fornecedorField.hidden = !isEntrada;
    observacaoField.hidden = !isAjuste;

    if (!isEntrada) {
      fornecedorSelect.value = '';
    }

    if (!isAjuste) {
      observacaoInput.value = '';
    }
  }

  function getMovementPayload() {
    const quantidadeInput = document.getElementById('quantidade');
    const quantidadeValue = quantidadeInput ? quantidadeInput.value.trim() : '0';

    return {
      produtoId: produtoSelect.value,
      tipo: tipoSelect.value,
      quantidade: parseInt(quantidadeValue, 10) || 0,
      fornecedorId: fornecedorSelect ? fornecedorSelect.value : '',
      observacao: observacaoInput ? observacaoInput.value.trim() : '',
    };
  }

  function validateMovement(payload, product) {
    if (!payload.produtoId || !payload.tipo) {
      return 'Selecione produto e tipo de movimentacao.';
    }

    if (!Number.isFinite(payload.quantidade) || payload.quantidade <= 0) {
      return 'Quantidade deve ser maior que zero.';
    }

    if (payload.tipo === 'ENTRADA' && !payload.fornecedorId) {
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

        // Garantir que quantidade é sempre um número
        const quantidade = Number.isFinite(movement.quantidade) ? movement.quantidade : 0;

        return `
          <tr>
            <td>${app.formatDateTime(movement.data)}</td>
            <td>${movement.produtoNome}</td>
            <td><span class="badge-status ${typeClass}">${movement.tipo}</span></td>
            <td>${quantidade}</td>
            <td>${app.formatMoney(movement.valor)}</td>
            <td>${detail}</td>
          </tr>
        `;
      })
      .join('');

    tableBody.innerHTML = rows || '<tr><td colspan="6">Nenhuma movimentacao registrada.</td></tr>';
  }

  function registerMovement(event) {
    event.preventDefault();

    const payload = getMovementPayload();
    const product = app.findProductById(payload.produtoId);
    const validationMessage = validateMovement(payload, product);
    if (validationMessage) {
      ui.showFeedback(feedback, validationMessage, 'error');
      return;
    }

    const supplier = payload.fornecedorId ? app.findSupplierById(payload.fornecedorId) : null;
    const quantity = payload.quantidade;
    const totalValue = quantity * (product.preco || 0);

    if (payload.tipo === 'ENTRADA') {
      product.quantidade += quantity;
    } else if (payload.tipo === 'SAIDA') {
      product.quantidade -= quantity;
    } else {
      product.quantidade += quantity;
    }

    state.movimentacoes.push({
      id: app.generateId('mov'),
      data: new Date().toISOString(),
      produtoId: product.id,
      produtoNome: product.nome,
      tipo: payload.tipo,
      quantidade,
      valor: totalValue,
      fornecedorId: supplier ? supplier.id : '',
      fornecedorNome: supplier ? supplier.nome : '',
      observacao: payload.observacao,
    });

    app.persistState();
    populateProducts();
    renderTable();
    form.reset();
    updateTypeFields();
    ui.showFeedback(feedback, 'Movimentacao registrada!', 'success');
  }

  function init() {
    if (!ui.requireSession() || !ui.requirePageAccess()) {
      return;
    }

    ui.setActiveNav();
    ui.bindLogoutButtons();
    ui.renderTransientNotice();
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