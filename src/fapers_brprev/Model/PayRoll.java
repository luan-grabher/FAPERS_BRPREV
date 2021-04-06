package fapers_brprev.Model;

import Dates.Dates;
import static fapers_brprev.FAPERS_BRPREV.month;
import static fapers_brprev.FAPERS_BRPREV.year;
import fileManager.FileManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sql.Database;

public class PayRoll {

    private static String sqlGetAccountingEntries = FileManager.getText("./sql/getAccountingEntries.sql");
    private static String date;

    /**
     * Retorna o que deve ser importado para o arquivo final
     *
     * @return
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
                    
                    
                    //Adiciona debito e credito
                    addImport(imports, date, cols, 19, 1, "C"); //PROVENTO
                    addImport(imports, date, cols, 47, 25, "D"); //DESCONTO
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
     * Retorna mapa do SQL
     */
    private static List<Map<String, Object>> getAccountingEntries() {
        Map<String, String> swaps = new HashMap<>();
        swaps.put("enterprise", "38");
        swaps.put("year", year.toString());
        swaps.put("month", month.toString());
        swaps.put("lastMonthDay", getLastDay().toString());

        return Database.getDatabase().getMap(sqlGetAccountingEntries, swaps);
    }

    /*
    * Adiciona a importação 
     */
    private static void addImport(List<Map<String, String>> imports, String date, String[] cols, Integer colValue, Integer colDescription, String debitCredit) {
        if (!cols[colValue].equals("") && !cols[colDescription].equals("")) {
            Map<String, String> toImport = Layout.getDefaultMap();
            toImport.put("descricaoHistorico", cols[colDescription]);

            /*Define o codigo do Historico padrao com base na descricao*/
            Map<String, Object> filter = Accounts.get(cols[colDescription]);
            toImport.put("historicoPadrao", filter != null ? String.valueOf(filter.get("historicoPadrao")) : "0");
            toImport.put("conta", filter != null ? String.valueOf(filter.get("conta")) : "0");

            toImport.put("valorLançamento", cols[colValue].replaceAll("[^0-9]+", ""));
            toImport.put("indicadorDebitoCredito", debitCredit);
            toImport.put("dataCD", date);
            toImport.put("dataDocumento", date);
            imports.add(toImport);
        }
    }

    /**
     * Retorna o último dia do mes e ano informado em integer
     */
    private static Integer getLastDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Retonr a o último dia do mes no formato ddMMyyyy
     */
    private static String getLastDate() {
        return monthWithZero(getLastDay()) + monthWithZero(month) + year;
    }

    /**
     * Retorna o mês com o zero na frente
     */
    private static String monthWithZero(Integer n) {
        return (n < 10 ? "0" : "") + n;
    }
}
