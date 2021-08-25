package fapers_brprev.Model;

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

    private static final String sqlGetAccountingEntries = FileManager.getText("./sql/get_FAPERS_monthEntries.sql");
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

        //Conecta ao banco de dados
        Database.setStaticObject(new Database("./sci.cfg"));

        if (Database.getDatabase().testConnection()) {
            //Pega lançamentos no unico
            List<Map<String, Object>> entries = getAccountingEntries();

            //Se nao estiver vazio
            if (!entries.isEmpty()) {
                //Percorre lançamentos
                entries.forEach((Map<String, Object> e) -> {
                    /**
                     * Pega historico e conta de debito e credito
                     */
                    String historico = (String) e.getOrDefault("HISTORICO", "");
                    String value = String.valueOf(e.getOrDefault("VALOR", "0.00"));
                    String debito = e.get("DEBITO") != null ? e.get("DEBITO").toString() : "";
                    String credito = e.get("CREDITO") != null ? e.get("CREDITO").toString() : "";

                    Map<String, Object> account = Accounts.get(historico, debito, credito);

                    //Verifica se retornou o mapa e se é um amapa valido que contem o hp
                    if (account != null && account.get("hp") != null) {
                        //Adiciona debito e credito
                        if (!((Map<String, String>) account.get("debit")).isEmpty()) {
                            addImport(value, historico, "D", (Map<String, String>) account.get("debit"),(String) account.get("hp"));
                        }

                        if (!((Map<String, String>) account.get("credit")).isEmpty()) {
                            addImport(value, historico, "C", (Map<String, String>) account.get("credit"),(String) account.get("hp"));
                        }
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
    private static void addImport(String value, String history, String debitCredit, Map<String, String> account, String historyCode) {
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
