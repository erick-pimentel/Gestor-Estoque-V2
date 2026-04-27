(function (window, document) {
  const app = window.GestorEstoque;
  const ui = window.GestorEstoqueUI;

  let filteredRows = [];
  let estoqueData = [];
  let produtosData = [];
  let fornecedoresData = [];

  // ============ ABA: MOVIMENTAÇÕES ============
  async function filterMovements() {
    const period = {
      start: document.getElementById('dataInicio').value,
      end: document.getElementById('dataFim').value,
    };

    if (!period.start || !period.end) {
      ui.showFeedback(document.getElementById('relatorioFeedback'), 'Informe data inicial e final.', 'error');
      return;
    }

    try {
      const rows = await app.getRelatorioMovimentacoes(period.start, period.end);
      filteredRows = Array.isArray(rows) ? rows : [];
      renderTableMovimentacoes(filteredRows);
      updateSummaryMovimentacoes(filteredRows);
      ui.showFeedback(document.getElementById('relatorioFeedback'), 'Relatorio atualizado.', 'success');
    } catch (error) {
      ui.handleApiError(error, document.getElementById('relatorioFeedback'));
    }
  }

  function updateSummaryMovimentacoes(rows) {
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

    const totalEntradas = document.getElementById('totalEntradas');
    const totalSaidas = document.getElementById('totalSaidas');
    const totalMovimentacoes = document.getElementById('totalMovimentacoes');

    if (totalEntradas) totalEntradas.textContent = String(entradas);
    if (totalSaidas) totalSaidas.textContent = String(saidas);
    if (totalMovimentacoes) totalMovimentacoes.textContent = String(movimentacoes);
  }

  function renderTableMovimentacoes(rows) {
    const tableBody = document.getElementById('relatoriosBody');
    if (!tableBody) return;

    const html = rows
      .slice()
      .sort((left, right) => String(left.semana).localeCompare(String(right.semana)))
      .map((row) => {
        const typeClass = row.tipo === 'SAIDA' ? 'badge-low' : row.tipo === 'AJUSTE' ? 'badge-warn' : 'badge-ok';

        // Debug log
        console.log('Movimentacao:', {
          semana: row.semana,
          produto: row.nomeProduto,
          tipo: row.tipo,
          valorTotal: row.valorTotal,
          quantidade: row.quantidade
        });

        const valorFormatado = row.valorTotal && (typeof row.valorTotal === 'number' || typeof row.valorTotal === 'string')
          ? app.formatMoney(row.valorTotal)
          : 'R$ 0,00';

        return `
          <tr>
            <td>${row.semana}</td>
            <td>${row.nomeProduto}</td>
            <td><span class="badge-status ${typeClass}">${row.tipo}</span></td>
            <td>${row.quantidade || 0}</td>
            <td>${valorFormatado}</td>
            <td>${valorFormatado}</td>
          </tr>
        `;
      })
      .join('');

    tableBody.innerHTML = html || '<tr><td colspan="6">Nenhuma movimentacao encontrada no periodo.</td></tr>';
  }

  // ============ ABA: ESTOQUE ============
  async function loadEstoque() {
    try {
      const rows = await app.getRelatorioEstoque();
      estoqueData = Array.isArray(rows) ? rows : [];
      renderTableEstoque(estoqueData);
      updateSummaryEstoque(estoqueData);
      ui.showFeedback(document.getElementById('relatorioFeedback'), 'Estoque carregado.', 'success');
    } catch (error) {
      ui.handleApiError(error, document.getElementById('relatorioFeedback'));
    }
  }

  function updateSummaryEstoque(rows) {
    let totalProdutos = rows.length;
    let valorTotal = rows.reduce((sum, item) => sum + (Number(item.valorTotalEstoque) || 0), 0);

    const totalProdutosEl = document.getElementById('totalProdutosEstoque');
    const valorTotalEl = document.getElementById('valorTotalEstoque');

    if (totalProdutosEl) totalProdutosEl.textContent = String(totalProdutos);
    if (valorTotalEl) valorTotalEl.textContent = app.formatMoney(valorTotal);
  }

  function renderTableEstoque(rows) {
    const tableBody = document.getElementById('estoqueBody');
    if (!tableBody) return;

    const html = rows
      .slice()
      .sort((left, right) => String(left.nomeProduto).localeCompare(String(right.nomeProduto)))
      .map((row) => {
        return `
          <tr>
            <td>${row.nomeProduto}</td>
            <td>${row.codigoProduto || '-'}</td>
            <td>${row.quantidadeAtual}</td>
            <td>${app.formatMoney(row.precoVenda)}</td>
            <td>${app.formatMoney(row.valorTotalEstoque)}</td>
          </tr>
        `;
      })
      .join('');

    tableBody.innerHTML = html || '<tr><td colspan="5">Nenhum produto no estoque.</td></tr>';
  }

  // ============ ABA: POR PRODUTO ============
  async function filterProdutos() {
    const period = {
      start: document.getElementById('dataInicioProduto').value,
      end: document.getElementById('dataFimProduto').value,
    };

    if (!period.start || !period.end) {
      ui.showFeedback(document.getElementById('relatorioFeedback'), 'Informe data inicial e final.', 'error');
      return;
    }

    try {
      const rows = await app.getRelatorioProdutos(period.start, period.end);
      produtosData = Array.isArray(rows) ? rows : [];
      renderTableProdutos(produtosData);
      ui.showFeedback(document.getElementById('relatorioFeedback'), 'Relatorio atualizado.', 'success');
    } catch (error) {
      ui.handleApiError(error, document.getElementById('relatorioFeedback'));
    }
  }

  function renderTableProdutos(rows) {
    const tableBody = document.getElementById('produtosBody');
    if (!tableBody) return;

    const html = rows
      .slice()
      .sort((left, right) => String(left.nomeProduto).localeCompare(String(right.nomeProduto)))
      .map((row) => {
        // Debug log
        console.log('Produto:', {
          nomeProduto: row.nomeProduto,
          valorMovimentado: row.valorMovimentado,
          totalEntradas: row.totalEntradas,
          totalSaidas: row.totalSaidas
        });

        const valorFormatado = row.valorMovimentado && (typeof row.valorMovimentado === 'number' || typeof row.valorMovimentado === 'string')
          ? app.formatMoney(row.valorMovimentado)
          : 'R$ 0,00';

        return `
          <tr>
            <td>${row.nomeProduto}</td>
            <td>${row.totalEntradas || 0}</td>
            <td>${row.totalSaidas || 0}</td>
            <td>${row.totalAjustes || 0}</td>
            <td>${row.quantidadeAtual || 0}</td>
            <td>${valorFormatado}</td>
          </tr>
        `;
      })
      .join('');

    tableBody.innerHTML = html || '<tr><td colspan="6">Nenhuma movimentacao encontrada no periodo.</td></tr>';
  }

  // ============ ABA: POR FORNECEDOR ============
  async function filterFornecedores() {
    const period = {
      start: document.getElementById('dataInicioFornecedor').value,
      end: document.getElementById('dataFimFornecedor').value,
    };

    if (!period.start || !period.end) {
      ui.showFeedback(document.getElementById('relatorioFeedback'), 'Informe data inicial e final.', 'error');
      return;
    }

    try {
      const rows = await app.getRelatorioFornecedores(period.start, period.end);
      fornecedoresData = Array.isArray(rows) ? rows : [];
      renderTableFornecedores(fornecedoresData);
      ui.showFeedback(document.getElementById('relatorioFeedback'), 'Relatorio atualizado.', 'success');
    } catch (error) {
      ui.handleApiError(error, document.getElementById('relatorioFeedback'));
    }
  }

  function renderTableFornecedores(rows) {
    const tableBody = document.getElementById('fornecedoresBody');
    if (!tableBody) return;

    const html = rows
      .slice()
      .sort((left, right) => String(left.nomeFornecedor).localeCompare(String(right.nomeFornecedor)))
      .map((row) => {
        // Debug log
        console.log('Fornecedor:', {
          nomeFornecedor: row.nomeFornecedor,
          valorTotalMovimentacoes: row.valorTotalMovimentacoes,
          totalMovimentacoes: row.totalMovimentacoes
        });

        const valorFormatado = row.valorTotalMovimentacoes && (typeof row.valorTotalMovimentacoes === 'number' || typeof row.valorTotalMovimentacoes === 'string')
          ? app.formatMoney(row.valorTotalMovimentacoes)
          : 'R$ 0,00';

        return `
          <tr>
            <td>${row.nomeFornecedor}</td>
            <td>${row.totalMovimentacoes || 0}</td>
            <td>${row.totalProdutos || 0}</td>
            <td>${valorFormatado}</td>
          </tr>
        `;
      })
      .join('');

    tableBody.innerHTML = html || '<tr><td colspan="4">Nenhuma movimentacao encontrada no periodo.</td></tr>';
  }

  // ============ SISTEMA DE ABAS ============
  function initTabs() {
    document.querySelectorAll('.tab-btn').forEach((btn) => {
      btn.addEventListener('click', () => {
        const tabName = btn.dataset.tab;
        showTab(tabName);
      });
    });
  }

  function showTab(tabName) {
    // Esconder todas as abas
    document.querySelectorAll('.report-content').forEach((tab) => {
      tab.classList.remove('active');
    });

    // Remover active de todos os botões
    document.querySelectorAll('.tab-btn').forEach((btn) => {
      btn.classList.remove('active');
    });

    // Mostrar a aba selecionada
    const tabEl = document.getElementById(`${tabName}-tab`);
    if (tabEl) {
      tabEl.classList.add('active');
    }

    // Marcar o botão como active
    const btnEl = document.querySelector(`[data-tab="${tabName}"]`);
    if (btnEl) {
      btnEl.classList.add('active');
    }
  }

  // ============ EXPORT PDF ============
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
    const totalEntradas = document.getElementById('totalEntradas');
    const totalSaidas = document.getElementById('totalSaidas');
    doc.text(`Total de entradas: ${totalEntradas ? totalEntradas.textContent : '0'}`, marginLeft, cursorY);
    doc.text(`Total de saidas: ${totalSaidas ? totalSaidas.textContent : '0'}`, marginLeft + 180, cursorY);
    cursorY += 18;

    const headers = ['Semana', 'Produto', 'Tipo', 'Quantidade', 'Valor Unitario', 'Valor Total'];
    const columnWidths = [60, 180, 70, 80, 90, 100];
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
        app.formatMoney(row.valorTotal),
      ];

      rowX = marginLeft;
      values.forEach((value, index) => {
        const text = String(value).slice(0, index === 1 ? 25 : 18);
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

  function exportPdfProdutos() {
    if (!window.jspdf || !window.jspdf.jsPDF) {
      ui.showFeedback(document.getElementById('relatorioFeedback'), 'Biblioteca de PDF indisponivel.', 'error');
      return;
    }

    const doc = new window.jspdf.jsPDF({ orientation: 'landscape', unit: 'pt', format: 'a4' });
    const marginLeft = 40;
    let cursorY = 40;

    doc.setFontSize(16);
    doc.text('Relatorio de Movimentacoes por Produto', marginLeft, cursorY);
    cursorY += 24;
    doc.setFontSize(9);

    const headers = ['Produto', 'Entradas', 'Saidas', 'Ajustes', 'Estoque Atual', 'Valor Total'];
    const columnWidths = [150, 60, 60, 60, 80, 120];
    let rowY = cursorY + 10;
    let rowX = marginLeft;

    headers.forEach((header, index) => {
      doc.text(header, rowX, rowY);
      rowX += columnWidths[index];
    });

    rowY += 12;
    doc.setLineWidth(0.5);
    doc.line(marginLeft, rowY - 8, marginLeft + columnWidths.reduce((sum, width) => sum + width, 0), rowY - 8);

    produtosData.forEach((row) => {
      const values = [
        row.nomeProduto,
        String(row.totalEntradas || 0),
        String(row.totalSaidas || 0),
        String(row.totalAjustes || 0),
        String(row.quantidadeAtual || 0),
        app.formatMoney(row.valorMovimentado),
      ];

      rowX = marginLeft;
      values.forEach((value, index) => {
        const text = String(value).slice(0, index === 0 ? 30 : 15);
        doc.text(text, rowX, rowY);
        rowX += columnWidths[index];
      });

      rowY += 16;
      if (rowY > 520) {
        doc.addPage();
        rowY = 40;
      }
    });

    doc.save('relatorio-produtos.pdf');
  }

  function exportPdfFornecedores() {
    if (!window.jspdf || !window.jspdf.jsPDF) {
      ui.showFeedback(document.getElementById('relatorioFeedback'), 'Biblioteca de PDF indisponivel.', 'error');
      return;
    }

    const doc = new window.jspdf.jsPDF({ orientation: 'landscape', unit: 'pt', format: 'a4' });
    const marginLeft = 40;
    let cursorY = 40;

    doc.setFontSize(16);
    doc.text('Relatorio de Movimentacoes por Fornecedor', marginLeft, cursorY);
    cursorY += 24;
    doc.setFontSize(9);

    const headers = ['Fornecedor', 'Total Movimentacoes', 'Total Produtos', 'Valor Total'];
    const columnWidths = [200, 100, 100, 150];
    let rowY = cursorY + 10;
    let rowX = marginLeft;

    headers.forEach((header, index) => {
      doc.text(header, rowX, rowY);
      rowX += columnWidths[index];
    });

    rowY += 12;
    doc.setLineWidth(0.5);
    doc.line(marginLeft, rowY - 8, marginLeft + columnWidths.reduce((sum, width) => sum + width, 0), rowY - 8);

    fornecedoresData.forEach((row) => {
      const values = [
        row.nomeFornecedor,
        String(row.totalMovimentacoes || 0),
        String(row.totalProdutos || 0),
        app.formatMoney(row.valorTotalMovimentacoes),
      ];

      rowX = marginLeft;
      values.forEach((value, index) => {
        const text = String(value).slice(0, index === 0 ? 35 : 20);
        doc.text(text, rowX, rowY);
        rowX += columnWidths[index];
      });

      rowY += 16;
      if (rowY > 520) {
        doc.addPage();
        rowY = 40;
      }
    });

    doc.save('relatorio-fornecedores.pdf');
  }

  // ============ INICIALIZAÇÃO ============
  function init() {
    if (!ui.requireSession() || !ui.requirePageAccess()) {
      return;
    }

    ui.setActiveNav();
    ui.bindLogoutButtons();
    ui.renderTransientNotice();
    initTabs();
    renderTableMovimentacoes([]);
    renderTableEstoque([]);
    renderTableProdutos([]);
    renderTableFornecedores([]);

    // Eventos: Movimentações
    const formMov = document.getElementById('filtroRelatorioForm');
    if (formMov) {
      formMov.addEventListener('submit', (event) => {
        event.preventDefault();
        filterMovements();
      });
    }

    const btnExportPdf = document.getElementById('exportarPdfBtn');
    if (btnExportPdf) {
      btnExportPdf.addEventListener('click', exportPdf);
    }

    // Eventos: Estoque
    const btnEstoque = document.getElementById('carregarEstoqueBtn');
    if (btnEstoque) {
      btnEstoque.addEventListener('click', loadEstoque);
    }

    // Eventos: Produtos
    const formProd = document.getElementById('filtroProdutoForm');
    if (formProd) {
      formProd.addEventListener('submit', (event) => {
        event.preventDefault();
        filterProdutos();
      });
    }

    const btnExportPdfProduto = document.getElementById('exportarPdfProdutoBtn');
    if (btnExportPdfProduto) {
      btnExportPdfProduto.addEventListener('click', exportPdfProdutos);
    }

    // Eventos: Fornecedores
    const formForn = document.getElementById('filtroFornecedorForm');
    if (formForn) {
      formForn.addEventListener('submit', (event) => {
        event.preventDefault();
        filterFornecedores();
      });
    }

    const btnExportPdfFornecedor = document.getElementById('exportarPdfFornecedorBtn');
    if (btnExportPdfFornecedor) {
      btnExportPdfFornecedor.addEventListener('click', exportPdfFornecedores);
    }
  }

  document.addEventListener('DOMContentLoaded', init);
})(window, document);

