package AppGestorEstoques.Service;

import AppGestorEstoques.Model.MovimentacaoDetalhesDTO;
import AppGestorEstoques.Model.RelatorioSemanalDTO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RelatorioPdfExporter implements RelatorioExporter {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void exportar(RelatorioSemanalDTO relatorio, List<MovimentacaoDetalhesDTO> detalhes, File destino) throws IOException {
        List<String> linhas = montarLinhas(relatorio, detalhes);
        String conteudo = montarConteudoPdf(linhas);
        byte[] pdf = montarArquivoPdf(conteudo);

        try (FileOutputStream fos = new FileOutputStream(destino)) {
            fos.write(pdf);
        }
    }

    private List<String> montarLinhas(RelatorioSemanalDTO relatorio, List<MovimentacaoDetalhesDTO> detalhes) {
        List<String> linhas = new ArrayList<>();
        NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

        linhas.add("Relatorio Semanal - Gestor Estoque");
        linhas.add("Periodo: " + relatorio.periodoInicio().format(DATE_FORMAT) + " a " + relatorio.periodoFim().format(DATE_FORMAT));
        linhas.add("Total Produtos: " + relatorio.totalProdutos());
        linhas.add("Produtos Vendidos: " + relatorio.produtosVendidos());
        // Normalize nbsp from currency formatter to plain space to avoid odd glyphs in basic PDF encoding.
        String faturamentoFormatado = moeda.format(relatorio.faturamento())
                .replace('\u00A0', ' ')
                .replace('&', ' ');
        linhas.add("Faturamento: " + faturamentoFormatado);
        linhas.add(" ");
        linhas.add("Detalhes de Movimentacao:");

        if (detalhes == null || detalhes.isEmpty()) {
            linhas.add("Nenhuma movimentacao encontrada para o periodo.");
            return linhas;
        }

        int maxLinhasDetalhes = 35;
        int limite = Math.min(detalhes.size(), maxLinhasDetalhes);
        for (int i = 0; i < limite; i++) {
            MovimentacaoDetalhesDTO item = detalhes.get(i);
            linhas.add("#" + item.idMovimentacao() +
                    " | " + item.tipo() +
                    " | Qtd: " + item.qtd() +
                    " | " + safe(item.produtoNome()) +
                    " | " + item.data().format(DATE_TIME_FORMAT));
        }

        if (detalhes.size() > maxLinhasDetalhes) {
            linhas.add("... " + (detalhes.size() - maxLinhasDetalhes) + " registro(s) adicional(is) omitido(s).");
        }

        return linhas;
    }

    private byte[] montarArquivoPdf(String streamContent) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();

        write(out, "%PDF-1.4\n");

        offsets.add(out.size());
        write(out, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        offsets.add(out.size());
        write(out, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

        offsets.add(out.size());
        write(out, "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n");

        offsets.add(out.size());
        write(out, "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");

        byte[] contentBytes = streamContent.getBytes(StandardCharsets.ISO_8859_1);
        offsets.add(out.size());
        write(out, "5 0 obj\n<< /Length " + contentBytes.length + " >>\nstream\n");
        out.writeBytes(contentBytes);
        write(out, "\nendstream\nendobj\n");

        int xrefPos = out.size();
        write(out, "xref\n0 6\n");
        write(out, "0000000000 65535 f \n");
        for (int off : offsets) {
            write(out, String.format("%010d 00000 n \n", off));
        }

        write(out, "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" + xrefPos + "\n%%EOF");
        return out.toByteArray();
    }

    private String montarConteudoPdf(List<String> linhas) {
        StringBuilder sb = new StringBuilder();
        sb.append("BT\n");
        sb.append("/F1 11 Tf\n");
        sb.append("40 805 Td\n");
        sb.append("14 TL\n");

        int maxLinhasPagina = 53;
        int limite = Math.min(linhas.size(), maxLinhasPagina);
        for (int i = 0; i < limite; i++) {
            sb.append("(").append(escapePdfText(linhas.get(i))).append(") Tj\nT*\n");
        }

        sb.append("ET");
        return sb.toString();
    }

    private String escapePdfText(String texto) {
        if (texto == null) {
            return "";
        }

        String escaped = texto
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");

        return escaped.replaceAll("[^\\x20-\\x7E\\xA0-\\xFF]", "?");
    }

    private String safe(String texto) {
        return texto == null ? "(Sem nome)" : texto;
    }

    private void write(ByteArrayOutputStream out, String value) {
        out.writeBytes(value.getBytes(StandardCharsets.ISO_8859_1));
    }
}


