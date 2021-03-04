package fapers_brprev.Model;

import fileManager.FileManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayRoll {

    public static List<Map<String, String>> getImports(File file) {
        List<Map<String, String>> imports = new ArrayList<>();

        //Pega texto do bagulho
        String[] lines = FileManager.getText(file).split("\r\n");

        Boolean get = Boolean.FALSE;

        for (String line : lines) {
            if (line.contains("PROVENTOS") && line.contains("DESCONTOS")) {
                get = Boolean.TRUE;

                //Se estiver depois de proventos e descontos
            } else if (get) {
                //Se a linha tiver alguma coisa
                if (!"".equals(line)) {
                    String[] cols = line.split(";");
                    Map<String, String> toImport = Layout.getDefaultMap();

                    //PROVENTO
                    if (!cols[1].equals("") && !cols[17].equals("")) {
                        toImport.put("observacao", cols[1]);
                        toImport.put("valorLançamento", cols[17].replaceAll("\\.", "").replaceAll(",", "\\."));
                        toImport.put("indicadorDebitoCredito", "C");
                    }

                    //DESCONTO
                    if (!cols[24].equals("") && !cols[46].equals("")) {
                        toImport.put("observacao", cols[24]);
                        toImport.put("valorLançamento", cols[46].replaceAll("\\.", "").replaceAll(",", "\\."));
                        toImport.put("indicadorDebitoCredito", "D");
                    }

                    imports.add(toImport);
                } else {
                    //Sai do for pois ja pegou os provendos e descontos
                    break;
                }
            }
        }

        return imports;
    }
}
