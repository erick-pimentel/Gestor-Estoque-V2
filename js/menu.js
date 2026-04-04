(function (window, document) {
  const app = window.GestorEstoque;
  const ui = window.GestorEstoqueUI;

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

    const today = new Date('2026-04-04T12:00:00.000Z');
    const todayString = today.toISOString().slice(0, 10);
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
    renderStats();
    renderLatestMovements();
  }

  document.addEventListener('DOMContentLoaded', init);
})(window, document);