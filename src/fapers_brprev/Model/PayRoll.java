package fapers_brprev.Model;

import static fapers_brprev.FAPERS_BRPREV.log;
import static fapers_brprev.FAPERS_BRPREV.month;
import static fapers_brprev.FAPERS_BRPREV.year;
import fileManager.FileManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sql.Database;

public class PayRoll {

    private static String sqlGetAccountingEntries = FileManager.getText("./sql/get_FAPERS_monthEntries.sql");
    private static String date;

    /**
     * COISAS PARA FAZER:
     * -
     */
    /**
     * Retorna o que deve ser importado para o arquivo final
     *
     * @return lista com mapa do que deve ser importado
     * @throws java.lang.Exception
     */
    public static List<Map<String, String>> getImports() throws Exception {
        //Inicia imports
        List<Map<String, String>> imports = new ArrayList<>();
        date = getLastDate();

        //Conecta ao banco de dados
        Database.setStaticObject(new Database("./sci.cfg"));

        if (Database.getDatabase().testConnection()) {
            //Pega lançamentos no unico
            List<Map<String, Object>> entries = getAccountingEntries();

            //Se nao estiver vazio
            if (!entries.isEmpty()) {
                //Percorre lançamentos
                entries.forEach((e) -> {
                    /**
                     * Pega historico e conta de debito e credito
                     */
                    String historico = (String) e.get("historico");
                    String value = String.valueOf(e.get("valor"));

                    Map<String, Object> account = Accounts.get(historico, e.get("debito").toString(), e.get("credito").toString());

                    if (account != null) {
                        //Adiciona debito e credito
                        addImport(imports, value, historico, "D", (String) account.get("debito"), (String) account.get("historicoPadrao"));
                        addImport(imports, value, historico, "C", (String) account.get("credito"), (String) account.get("historicoPadrao"));
                    }else{
                        log.append("\nNão foi encontrado no arquivo de contas o "
                                + "HP, credito e debito para o lcto com Historico '")
                                .append(historico)
                                .append("', credito (").append(e.get("credito").toString())
                                .append("), debito (").append(e.get("debito").toString());
                    }
                });
            } else {
                throw new Exception("Nenhum lançamento encontrado neste mês!");
            }

            return imports;
        } else {
            throw new Exception("Erro ao conectar ao banco de dados!");
        }
    }

    /**
     * Retorna mapa do SQL com lctos do mes
     */
    private static List<Map<String, Object>> getAccountingEntries() {
        Map<String, String> swaps = new HashMap<>();
        swaps.put("enterprise", "38");
        swaps.put("year", year.toString());
        swaps.put("month", month.toString());
        swaps.put("lastDayOfMonth", getLastDay().toString());

        return Database.getDatabase().getMap(sqlGetAccountingEntries, swaps);
    }

    /*
    * Adiciona a importação 
     */
    private static void addImport(List<Map<String, String>> imports, String value, String history, String debitCredit, String historyCode, String account) {
        Map<String, String> toImport = Layout.getDefaultMap();
        toImport.put("descricaoHistorico", history);

        toImport.put("historicoPadrao", historyCode);
        toImport.put("conta", account);

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
        cal.set(Calendar.MONTH, month);
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
