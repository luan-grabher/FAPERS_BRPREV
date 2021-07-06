package fapers_brprev.Model;

import static fapers_brprev.FAPERS_BRPREV.log;
import fileManager.FileManager;
import fileManager.StringFilter;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Accounts {

    private static final Map<String, String> notFind = new HashMap<>();

    private static final Map<String, String> accountsMap = new HashMap<>();
    private static final Map<StringFilter, String> hpMap = new HashMap<>();

    /*Cria mapa com endereçamento da fapers para a conta do único*/
    public static void createAccountsMap(File file) {
        String[] rows = FileManager.getText(file).split("\r\n");

        for (String row : rows) {
            if (!row.startsWith("#")) {
                String[] cols = row.split(";", -1);

                if (!"".equals(cols[0]) && !"".equals(cols[1])) {
                    /*
                        0 - Conta unico
                        1 - Conta fapers 
                     */
                    accountsMap.put(cols[0], cols[1]);
                }
            }
        }
    }

    /*Cria mapa com filtros e seus historicos padroes*/
    public static void createHpMap(File file) {
        String[] rows = FileManager.getText(file).split("\r\n");

        for (String row : rows) {
            if (!row.startsWith("#")) {
                String[] cols = row.split(";", -1);

                if (!"".equals(cols[0]) && !"".equals(cols[1])) {
                    /*
                        0 - filtro hp
                        1 - fapers codigo historico 
                     */
                    hpMap.put(new StringFilter(cols[0].replaceAll(" ", ";")), cols[1]);
                }
            }
        }
    }

    /**
     * Retorna o objeto do mapa se o filtro bater
     *
     * @param history Historico unico
     * @param debit Conta do unico debito, para ignorar deixe null
     * @param credit Conta do unico credit, para ignorar deixe null
     * @return objeto do mapa se o filtro bater
     */
    public static Map<String, Object> get(String history, String debit, String credit) {
        if (isZero(debit) || accountsMap.containsKey(debit)) {
            if (isZero(credit) || accountsMap.containsKey(credit)) {
                //Procura historico
                for (Map.Entry<StringFilter, String> entry : hpMap.entrySet()) {
                    StringFilter filter = entry.getKey();
                    String hp = entry.getValue();

                    if (filter.filterOfString(history)) {
                        Map<String, String> r = new HashMap<>();
                        r.put("debit", accountsMap.getOrDefault(debit, null));
                        r.put("credit", accountsMap.getOrDefault(credit, null));
                        r.put("hp", accountsMap.get(hp));
                    }
                }

                //Se não encontrar o historico
                notFind.put(history, "Historico");
                return null;
            } else {
                notFind.put(credit, "Conta");
                return null;
            }
        } else {
            notFind.put(debit, "Conta");
            return null;
        }
    }

    public static void printNotFindInLog() {
        notFind.forEach((what, type) -> {
            log.append("\r\n").append(type).append(" '").append(what).append("'(UNICO) não encontrado nos arquivos DE_PARA.");
        });
    }

    /**
     * Retorna se um número String não é null, em branco ou zero
     */
    private static Boolean isZero(String numberStr) {
        return numberStr == null || numberStr.equals("") || numberStr.trim().equals("0");
    }
}
