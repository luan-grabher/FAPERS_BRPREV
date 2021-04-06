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
                map.put("debito", Integer.valueOf(cols[0]));
                map.put("credito", Integer.valueOf(cols[1]));
                map.put("historicoPadrao", Integer.valueOf(cols[2]));
                map.put("filtro", new StringFilter(cols[3].replaceAll(" ", ";")));
                map.put("unicoDebito", Integer.valueOf(cols[4]));
                map.put("unicoCredito", Integer.valueOf(cols[5]));

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
    public static Map<String, Object> get(String history, Integer debit, Integer credit) {
        Object[] obj = new Object[]{null};

        list.forEach((m) -> {
            if (((StringFilter) m.get("filtro")).filterOfString(history)
                    && ((debit != null && debit.equals(m.get("unicoDebito")))
                    || (credit != null && debit.equals(m.get("unicoCredito"))))) {
                obj[0] = m;
            }
        });

        return (Map<String, Object>) obj[0];
    }
}
