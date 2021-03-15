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
                map.put("conta", Integer.valueOf(cols[0]));
                map.put("historicoPadrao", Integer.valueOf(cols[1]));
                map.put("filtro", new StringFilter(cols[2].replaceAll(" ", ";")));

                list.add(map);
            }
        }
    }

    /**
     * Retorna a conta do mapa se o filtro bater
     *
     * @param filter Filtro de string
     * @return conta do mapa se o filtro bater
     */
    public static Integer getAccount(String filter) {
        return get("conta", filter);
    }

    /**
     * Retorna o historicoPadrao do mapa se o filtro bater
     *
     * @param filter Filtro de string
     * @return historicoPadrao do mapa se o filtro bater
     */
    public static Integer getDefaultHistory(String filter) {
        return get("historicoPadrao", filter);
    }

    /**
     * Retorna o objeto do mapa se o filtro bater
     *
     * @param key Nome do objeto procurado (conta ou historicoPadrao)
     * @param filter Filtro de string
     * @return objeto do mapa se o filtro bater
     */
    public static Integer get(String key, String filter) {
        Integer[] r = new Integer[]{0};

        list.forEach((m) -> {
            if (((StringFilter) m.get("filtro")).filterOfString(filter)) {
                r[0] = (Integer) m.get(key);
            }
        });

        return r[0];
    }
}
