package fapers_brprev.Model;

import fileManager.CSV;
import fileManager.FileManager;
import fileManager.StringFilter;
import java.io.File;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Accounts {

    public static final Map<String, String> notFind = new HashMap<>();
    private static final Map<String, Map<String, String>> accountsMap = new HashMap<>();
    private static final Map<StringFilter, String> hpMap = new HashMap<>();

    /*Cria mapa com endereçamento da fapers para a conta do único*/
    public static void createAccountsMap(File file) {
        String[] rows = FileManager.getText(file).split("\r\n");
        
        //Pega as linhas do csv em mapas
        List<Map<String, String>> csvAccounts = CSV.getMap(file);
        
        for (Map<String, String> csvAccount : csvAccounts) {
            //coloca no mapa de contas o mapa CSV da conta
            accountsMap.put(csvAccount.get("UNICO"), csvAccount);
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
                    hpMap.put(new StringFilter(removerAcentos(cols[0]).trim().replaceAll(" ", ";")), cols[1]);
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
        //Procura conta de debito
        if (isZero(debit) || accountsMap.containsKey(debit)) {
            //Procura conta de credito
            if (isZero(credit) || accountsMap.containsKey(credit)) {
                
                Map<String, String> debitMap = accountsMap.getOrDefault(debit, new HashMap<>());
                Map<String, String> creditMap = accountsMap.getOrDefault(credit, new HashMap<>());
                
                //Procura historico
                history = removerAcentos(history).trim();
                for (Map.Entry<StringFilter, String> entry : hpMap.entrySet()) {
                    StringFilter filter = entry.getKey();
                    String hp = entry.getValue();
                    //Se achar o historico na lista
                    if (filter.filterOfString(history)) {
                        //Se não for para ignorar alguma conta ou historico
                        if (!"IGNORAR".equals(debitMap.getOrDefault("FAPERS", ""))
                                && !"IGNORAR".equals(creditMap.getOrDefault("FAPERS", ""))
                                && !"IGNORAR".equals(hp)) {
                            
                            Map<String, Object> r = new HashMap<>();
                            r.put("debit", debitMap);
                            r.put("credit", creditMap);
                            r.put("hp", hp);

                            return r;
                        } else {
                            return null;
                        }
                    }
                }

                //Se não encontrar o historico
                notFind.put(history.replaceAll("[^a-zA-Z ]", "").split(" col ")[0].trim(), "Historico");
            } else {
                notFind.put(credit, "Conta");
            }
        } else {
            notFind.put(debit, "Conta");
        }

        return null;
    }

    /**
     *
     * @param accountsFile Arquivo CSV das contas
     * @param hpFile Arquivo CSV dos historicos
     */
    public static void notFindToFiles(File accountsFile, File hpFile) {
        //Se tiver algum não encontrado
        if (notFind.size() > 0) {
            //Pega texto dos arquivos
            StringBuilder accountsText = new StringBuilder(FileManager.getText(accountsFile));
            StringBuilder hpText = new StringBuilder(FileManager.getText(hpFile));

            //Adiciona abaixo do texto as linhas nao encontradas
            notFind.forEach((what, type) -> {
                if (type.equals("Historico")) {
                    hpText.append("\r\n").append(what).append(";");
                } else {
                    accountsText.append("\r\n").append(what).append(";;");
                }
            });

            //Salva os arquivos
            FileManager.save(accountsFile, accountsText.toString());
            FileManager.save(hpFile, hpText.toString());
        }
    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    /**
     * Retorna se um número String não é null, em branco ou zero
     */
    private static Boolean isZero(String numberStr) {
        return numberStr == null || numberStr.equals("") || numberStr.trim().equals("0");
    }
}
