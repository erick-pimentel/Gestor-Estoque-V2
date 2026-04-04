(function (window, document) {
  const app = window.GestorEstoque;
  const ui = window.GestorEstoqueUI;

  const state = app.state;
  const form = document.getElementById('filtroRelatorioForm');
  const tableBody = document.getElementById('relatoriosBody');
  const totalEntradas = document.getElementById('totalEntradas');
  const totalSaidas = document.getElementById('totalSaidas');
  const totalMovimentacoes = document.getElementById('totalMovimentacoes');
  const exportButton = document.getElementById('exportarPdfBtn');

  let filteredMovements = [];

  function getPeriod() {
    const startValue = document.getElementById('dataInicio').value;
    const endValue = document.getElementById('dataFim').value;
    return {
      start: startValue ? new Date(`${startValue}T00:00:00`) : null,
      end: endValue ? new Date(`${endValue}T23:59:59`) : null,
    };
  }

  function filterMovements() {
    const period = getPeriod();

    filteredMovements = state.movimentacoes.filter((movement) => {
      const movementDate = new Date(movement.data);
      if (period.start && movementDate < period.start) {
        return false;
      }
      if (period.end && movementDate > period.end) {
        return false;
      }
      return true;
    });

    renderTable(filteredMovements);
    updateSummary(filteredMovements);
    ui.showFeedback(document.getElementById('relatorioFeedback'), 'Relatorio atualizado.', 'success');
  }

  function updateSummary(movements) {
    const entradas = movements.filter((movement) => movement.tipo === 'ENTRADA').reduce((sum, movement) => sum + movement.quantidade, 0);
    const saidas = movements.filter((movement) => movement.tipo === 'SAIDA').reduce((sum, movement) => sum + movement.quantidade, 0);

    if (totalEntradas) {
      totalEntradas.textContent = String(entradas);
    }

    if (totalSaidas) {
      totalSaidas.textContent = String(saidas);
    }

    if (totalMovimentacoes) {
      totalMovimentacoes.textContent = String(movements.length);
    }
  }

  function renderTable(movements) {
    if (!tableBody) {
      return;
    }

    const rows = movements
      .slice()
      .sort((left, right) => new Date(left.data) - new Date(right.data))
      .map((movement) => {
        const typeClass = movement.tipo === 'SAIDA' ? 'badge-low' : movement.tipo === 'AJUSTE' ? 'badge-warn' : 'badge-ok';
        return `
          <tr>
            <td>${app.getWeekNumber(movement.data)}</td>
            <td>${movement.produtoNome}</td>
            <td><span class="badge-status ${typeClass}">${movement.tipo}</span></td>
            <td>${movement.quantidade}</td>
            <td>${app.formatMoney(movement.valor)}</td>
          </tr>
        `;
      })
      .join('');

    tableBody.innerHTML = rows || '<tr><td colspan="5">Nenhuma movimentacao encontrada no periodo.</td></tr>';
  }

  function exportPdf() {
    if (!window.jspdf || !window.jspdf.jsPDF) {
      ui.showFeedback(document.getElementById('relatorioFeedback'), 'Biblioteca de PDF indisponivel.', 'error');
      return;
    }

    const doc = new window.jspdf.jsPDF({ orientation: 'landscape', unit: 'pt', format: 'a4' });
    const marginLeft = 40;
    let cursorY = 40;

    doc.setFontSize(16);
    doc.text('Relatorio de movimentacoes', marginLeft, cursorY);
    cursorY += 24;
    doc.setFontSize(10);
    doc.text(`Total de entradas: ${totalEntradas ? totalEntradas.textContent : '0'}`, marginLeft, cursorY);
    doc.text(`Total de saidas: ${totalSaidas ? totalSaidas.textContent : '0'}`, marginLeft + 180, cursorY);
    cursorY += 18;

    const headers = ['Semana', 'Produto', 'Tipo', 'Quantidade', 'Valor'];
    const columnWidths = [70, 210, 90, 90, 100];
    let rowY = cursorY + 10;
    let rowX = marginLeft;

    doc.setFontSize(9);
    headers.forEach((header, index) => {
      doc.text(header, rowX, rowY);
      rowX += columnWidths[index];
    });

    rowY += 12;
    doc.setLineWidth(0.5);
    doc.line(marginLeft, rowY - 8, marginLeft + columnWidths.reduce((sum, width) => sum + width, 0), rowY - 8);

    filteredMovements.forEach((movement) => {
      const values = [
        app.getWeekNumber(movement.data),
        movement.produtoNome,
        movement.tipo,
        String(movement.quantidade),
        app.formatMoney(movement.valor),
      ];

      rowX = marginLeft;
      values.forEach((value, index) => {
        const text = String(value).slice(0, index === 1 ? 34 : 18);
        doc.text(text, rowX, rowY);
        rowX += columnWidths[index];
      });

      rowY += 16;
      if (rowY > 520) {
        doc.addPage();
        rowY = 40;
      }
    });

    doc.save('relatorio-movimentacoes.pdf');
  }

  function init() {
    if (!ui.requireSession() || !ui.requirePageAccess()) {
      return;
    }

    ui.setActiveNav();
    ui.bindLogoutButtons();
    ui.renderTransientNotice();
    filteredMovements = state.movimentacoes.slice();
    renderTable(filteredMovements);
    updateSummary(filteredMovements);

    if (form) {
      form.addEventListener('submit', (event) => {
        event.preventDefault();
        filterMovements();
      });
    }

    if (exportButton) {
      exportButton.addEventListener('click', exportPdf);
    }
  }

  document.addEventListener('DOMContentLoaded', init);
})(window, document);