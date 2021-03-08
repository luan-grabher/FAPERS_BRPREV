package fapers_brprev.Model;

import Dates.Dates;
import fileManager.FileManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayRoll {

    public static List<Map<String, String>> getImports(File file) {
        List<Map<String, String>> imports = new ArrayList<>();

        //Pega texto do bagulho
        String[] lines = FileManager.getText(file).split("\r\n");

        String date = getLastDateOfMonth(lines[0]);

        Boolean get = Boolean.FALSE;

        for (String line : lines) {
            if (line.matches("PROVENTOS ;*DESCONTOS;*")) {
                get = Boolean.TRUE;

                //Se estiver depois de proventos e descontos
            } else if (get) {
                //Se a linha tiver alguma coisa
                if (!"".equals(line)) {
                    String[] cols = line.split(";", -1);
                    if (cols.length == 49) {
                        putVal(imports, date, cols, 19, 1, "C"); //PROVENTO
                        putVal(imports, date, cols, 47, 25, "D"); //DESCONTO
                    }
                } else {
                    //Sai do for pois ja pegou os provendos e descontos
                    break;
                }
            }
        }

        return imports;
    }

    private static void putVal(List<Map<String, String>> imports, String date, String[] cols, Integer colValue, Integer colDescription, String debitCredit) {
        if (!cols[colValue].equals("") && !cols[colDescription].equals("")) {
            Map<String, String> toImport = Layout.getDefaultMap();
            toImport.put("descricaoHistorico", cols[colDescription]);
            toImport.put("valorLançamento", cols[colValue].replaceAll("\\.", "").replaceAll(",", "\\."));
            toImport.put("indicadorDebitoCredito", debitCredit);
            toImport.put("dataCD", date);
            toImport.put("dataDocumento", date);
            imports.add(toImport);
        }
    }

    private static String getLastDateOfMonth(String lineOne) {
        Pattern pattern = Pattern.compile("[A-Z]+\\/202[1-9]");
        Matcher matcher = pattern.matcher(lineOne);
        Integer month = 0;
        Integer year = 2021;

        if (matcher.find()) {
            String[] monthAndYear = matcher.group().split("/");

            year = Integer.valueOf(monthAndYear[1]);
            month = Dates.getBrazilianMonths().indexOf(monthAndYear[0]);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, 1);

            Integer lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            return monthWithZero(lastDay) + monthWithZero(month) + year;
        } else {
            return "01012021";
        }
    }

    private static String monthWithZero(Integer n) {
        return (n < 10 ? "0" : "") + n;
    }
}
