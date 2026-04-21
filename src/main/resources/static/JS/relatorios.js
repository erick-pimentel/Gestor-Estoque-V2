(function (window, document) {
  const app = window.GestorEstoque;
  const ui = window.GestorEstoqueUI;

  const form = document.getElementById('filtroRelatorioForm');
  const tableBody = document.getElementById('relatoriosBody');
  const totalEntradas = document.getElementById('totalEntradas');
  const totalSaidas = document.getElementById('totalSaidas');
  const totalMovimentacoes = document.getElementById('totalMovimentacoes');
  const exportButton = document.getElementById('exportarPdfBtn');

  let filteredRows = [];

  function getPeriod() {
    const startValue = document.getElementById('dataInicio').value;
    const endValue = document.getElementById('dataFim').value;
    return {
      start: startValue,
      end: endValue,
    };
  }

  async function filterMovements() {
    const period = getPeriod();

    if (!period.start || !period.end) {
      ui.showFeedback(document.getElementById('relatorioFeedback'), 'Informe data inicial e final.', 'error');
      return;
    }

    try {
      const rows = await app.getRelatorioMovimentacoes(period.start, period.end);
      filteredRows = Array.isArray(rows) ? rows : [];

      renderTable(filteredRows);
      updateSummary(filteredRows);
      ui.showFeedback(document.getElementById('relatorioFeedback'), 'Relatorio atualizado.', 'success');
    } catch (error) {
      ui.handleApiError(error, document.getElementById('relatorioFeedback'));
    }
  }

  function updateSummary(rows) {
    const weekly = new Map();

    rows.forEach((row) => {
      if (!weekly.has(row.semana)) {
        weekly.set(row.semana, {
          entradas: Number(row.totalEntradas) || 0,
          saidas: Number(row.totalSaidas) || 0,
          movimentacoes: Number(row.totalMovimentacoes) || 0,
        });
      }
    });

    let entradas = 0;
    let saidas = 0;
    let movimentacoes = 0;
    weekly.forEach((item) => {
      entradas += item.entradas;
      saidas += item.saidas;
      movimentacoes += item.movimentacoes;
    });

    if (totalEntradas) {
      totalEntradas.textContent = String(entradas);
    }

    if (totalSaidas) {
      totalSaidas.textContent = String(saidas);
    }

    if (totalMovimentacoes) {
      totalMovimentacoes.textContent = String(movimentacoes);
    }
  }

  function renderTable(rows) {
    if (!tableBody) {
      return;
    }

    const html = rows
      .slice()
      .sort((left, right) => String(left.semana).localeCompare(String(right.semana)))
      .map((row) => {
        const typeClass = row.tipo === 'SAIDA' ? 'badge-low' : row.tipo === 'AJUSTE' ? 'badge-warn' : 'badge-ok';
        return `
          <tr>
            <td>${row.semana}</td>
            <td>${row.nomeProduto}</td>
            <td><span class="badge-status ${typeClass}">${row.tipo}</span></td>
            <td>${row.quantidade}</td>
            <td>${app.formatMoney(row.valorTotal)}</td>
          </tr>
        `;
      })
      .join('');

    tableBody.innerHTML = html || '<tr><td colspan="5">Nenhuma movimentacao encontrada no periodo.</td></tr>';
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

    filteredRows.forEach((row) => {
      const values = [
        row.semana,
        row.nomeProduto,
        row.tipo,
        String(row.quantidade),
        app.formatMoney(row.valorTotal),
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
    renderTable([]);
    updateSummary([]);

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