package fapers_brprev.Model;

import static fapers_brprev.FAPERS_BRPREV.month;
import static fapers_brprev.FAPERS_BRPREV.year;
import static fapers_brprev.FAPERS_BRPREV.unicoFolhaTxt;

import fileManager.CSV;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayRoll {

    private static String date;
    private static final List<Map<String, String>> imports = new ArrayList<>();

    /**
     * COISAS PARA FAZER: -
     */
    /**
     * Retorna o que deve ser importado para o arquivo final
     *
     * @return lista com mapa do que deve ser importado
     * @throws java.lang.Exception
     */
    public static List<Map<String, String>> getImports() throws Exception {

        date = getLastDate();

        List<Map<String, Object>> entries = getAccountingEntries();
        if (entries.isEmpty())
            throw new Exception("Nenhum lançamento encontrado desntro do arquivo neste mês!");

        // Percorre lançamentos
        for (Map<String, Object> e : entries) {
            String historico = (String) e.getOrDefault("HISTORICO", "");
            String value = String.valueOf(e.getOrDefault("VALOR", "0.00"));
            String debito = e.get("DEBITO") != null ? e.get("DEBITO").toString() : "";
            String credito = e.get("CREDITO") != null ? e.get("CREDITO").toString() : "";

            Map<String, Map<String, String>> normalizedHistoricos_ContasContabeis = Accounts.get(historico, debito, credito);

            Boolean isErrorOnNormalize = normalizedHistoricos_ContasContabeis == null;
            if(isErrorOnNormalize)
                continue;
            
            Boolean hasHistoricoPadrao = !normalizedHistoricos_ContasContabeis.get("hp").isEmpty();
            if(!hasHistoricoPadrao)
                continue;

            acImport("debit",  value, historico, normalizedHistoricos_ContasContabeis);
            acImport("credit", value, historico, normalizedHistoricos_ContasContabeis);
        };

        return imports;
    }

    /**
     * Adiciona a importação para o tipo de conta definido
     */
    private static void acImport(String type, String value, String history, Map<String, Map<String, String>> normalizedHistoricos_ContasContabeis) {
        Map<String, String> accountMap = (Map<String, String>) normalizedHistoricos_ContasContabeis.get(type);
        Map<String, String> hpMap = (Map<String, String>) normalizedHistoricos_ContasContabeis.get("hp");
        String historicoPadrao = hpMap.get("hp");

        Boolean isTypeEmpty = accountMap.isEmpty();
        if (isTypeEmpty)
            return;

        String tipoContaNormalizado  = type.substring(0, 1).toUpperCase();
        addImport(value, history, tipoContaNormalizado, accountMap, historicoPadrao);    
    }

    /**
     * Retorna mapa do SQL com lctos do mes
     */
    private static List<Map<String, Object>> getAccountingEntries() {
        List<Map<String, Object>> entries = new ArrayList<>();

        List<Map<String, String>> folhaDePagamentoUnico = CSV.getMap(unicoFolhaTxt, ";");
        for (Map<String, String> folha : folhaDePagamentoUnico) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("DEBITO", folha.get("3"));
            entry.put("CREDITO", folha.get("4"));
            entry.put("VALOR", folha.get("5"));
            entry.put("HISTORICO", folha.get("7"));

            Boolean isHeader = entry.get("HISTORICO") == "Histórico";
            if (isHeader)
                continue;

            entries.add(entry);
        }

        return entries;
    }

    /*
     * Adiciona a importação
     */
    private static void addImport(String value, String history, String debitCredit, Map<String, String> account,
            String historyCode) {
        Map<String, String> toImport = Layout.getDefaultMap();
        toImport.put("descricaoHistorico", history);

        toImport.put("historicoPadrao", historyCode);
        toImport.put("conta", account.get("FAPERS"));
        toImport.put("centroCusteio", account.get("CENTRO CUSTEIO"));

        toImport.put("valorLançamento", value.replaceAll("[^0-9]+", ""));
        toImport.put("indicadorDebitoCredito", debitCredit);
        toImport.put("dataCD", date);
        toImport.put("dataDocumento", date);
        imports.add(toImport);
    }

    /**
     * Retorna o último dia do mes e ano informado em Calendar
     */
    private static Calendar getLastCal() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        return cal;
    }

    /**
     * Retorna o último dia do mes e ano informado em integer
     */
    private static Integer getLastDay() {
        return getLastCal().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Retonr a o último dia do mes no formato ddMMyyyy
     */
    private static String getLastDate() {
        return zeroPrepend(getLastDay()) + zeroPrepend(month) + year;
    }

    /**
     * Retorna o numero com o zero na frente se for menor que 10
     */
    private static String zeroPrepend(Integer n) {
        return (n < 10 ? "0" : "") + n;
    }
}
