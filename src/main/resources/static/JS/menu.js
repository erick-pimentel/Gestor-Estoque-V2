(function (window, document) {
  const app = window.GestorEstoque;
  const ui = window.GestorEstoqueUI;

  function toMovementView(item) {
    const valorUnitario = Number(item.valorUnitario) || 0;
    return {
      data: item.dataMovimentacao,
      produtoNome: item.nomeProduto,
      tipo: item.tipo,
      quantidade: Number(item.quantidade) || 0,
      valor: valorUnitario * (Number(item.quantidade) || 0),
    };
  }

  async function loadDashboardData() {
    const fornecedoresPromise = app.listFornecedores().catch((error) => {
      if (error && error.status === 403) {
        return [];
      }
      throw error;
    });

    const [produtos, fornecedores, movimentacoes] = await Promise.all([
      app.listProdutos(),
      fornecedoresPromise,
      app.listMovimentacoes(),
    ]);

    app.setCollectionState('produtos', produtos);
    app.setCollectionState('fornecedores', fornecedores);
    app.setCollectionState('movimentacoes', movimentacoes.map(toMovementView));
  }

  function renderStats() {
    const productsCard = document.getElementById('totalProdutos');
    const suppliersCard = document.getElementById('totalFornecedores');
    const movementsCard = document.getElementById('movimentacoesDia');

    if (productsCard) {
      productsCard.textContent = String(app.state.produtos.length);
    }

    if (suppliersCard) {
      suppliersCard.textContent = String(app.state.fornecedores.length);
    }

    const todayString = new Date().toISOString().slice(0, 10);
    const todayMovements = app.state.movimentacoes.filter((movement) => movement.data.slice(0, 10) === todayString);

    if (movementsCard) {
      movementsCard.textContent = String(todayMovements.length);
    }
  }

  function renderLatestMovements() {
    const tableBody = document.getElementById('movimentacoesRecentesBody');
    if (!tableBody) {
      return;
    }

    const rows = app.state.movimentacoes
      .slice()
      .sort((left, right) => new Date(right.data) - new Date(left.data))
      .slice(0, 5)
      .map((movement) => {
        const typeClass = movement.tipo === 'SAIDA' ? 'badge-low' : movement.tipo === 'AJUSTE' ? 'badge-warn' : 'badge-ok';
        return `
          <tr>
            <td>${app.formatDateTime(movement.data)}</td>
            <td>${movement.produtoNome}</td>
            <td><span class="badge-status ${typeClass}">${movement.tipo}</span></td>
            <td>${movement.quantidade}</td>
            <td>${app.formatMoney(movement.valor)}</td>
          </tr>
        `;
      })
      .join('');

    tableBody.innerHTML = rows || '<tr><td colspan="5">Nenhuma movimentacao registrada.</td></tr>';
  }

  function init() {
    if (!ui.requireSession() || !ui.requirePageAccess()) {
      return;
    }

    ui.setActiveNav();
    ui.bindLogoutButtons();
    ui.renderTransientNotice();
    loadDashboardData()
      .then(() => {
        renderStats();
        renderLatestMovements();
      })
      .catch((error) => {
        ui.handleApiError(error);
      });
  }

  document.addEventListener('DOMContentLoaded', init);
})(window, document);