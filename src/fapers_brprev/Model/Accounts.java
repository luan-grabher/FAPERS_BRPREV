package fapers_brprev.Model;

import fileManager.FileManager;
import fileManager.StringFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Accounts {

    private static List<Map<String, Object>> list = new ArrayList<>();

    public static void addOnList(File file) {
        String text = FileManager.getText(file);
        String[] lines = text.split("\r\n");

        for (String line : lines) {
            if (!line.startsWith("#")) {
                String[] cols = line.split(";", -1);

                Map<String, Object> map = new HashMap<>();
                map.put("debito", cols[0]);
                map.put("credito", cols[1]);
                map.put("historicoPadrao", cols[2]);
                map.put("filtro", new StringFilter(cols[3].replaceAll(" ", ";")));
                map.put("unicoDebito", cols[4]);
                map.put("unicoCredito", cols[5]);

                list.add(map);
            }
        }
    }

    /**
     * Retorna o objeto do mapa se o filtro bater
     *
     * @param history Filtro de string
     * @param debit Conta do unico debito, para ignorar deixe null
     * @param credit Conta do unico credit, para ignorar deixe null
     * @return objeto do mapa se o filtro bater
     */
    public static Map<String, Object> get(String history, String debit, String credit) {
        Object[] obj = new Object[]{null};

        list.forEach((m) -> {
            String unicoDebito = (String) m.get("unicoDebito");
            String unicoCredito = (String) m.get("unicoCredito");

            if ( //O filtro bate com o historico
                    ((StringFilter) m.get("filtro")).filterOfString(history)
                    && ( //Ou a Conta de débito é igual a do unico
                    unicoDebito.equals(debit)
                    //Ou a conta de credito é igual a do unico
                    || unicoCredito.equals(credit))) {
                obj[0] = m;
            }
        });

        return (Map<String, Object>) obj[0];
    }
}
