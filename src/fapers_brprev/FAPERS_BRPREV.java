package fapers_brprev;

import fapers_brprev.Model.Layout;
import fapers_brprev.Model.PayRoll;
import fileManager.FileManager;
import fileManager.Selector;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class FAPERS_BRPREV {

    public static void main(String[] args) {
        try {
            File payRoll = getFile("Relatório Espelho Resumo Folha de pagamento CSV");
            File file2 = getFile("2");
            File file3 = getFile("3");
            
            //Mapa com importações em String para fazer
            List<Map<String,String>> imports = new ArrayList<>();
            
            //Extrai com o modelo dos arquivos as importações
            imports.addAll(PayRoll.getImports(payRoll));
            imports.addAll(null);
            imports.addAll(null);
            
            //Salva o arquivo de texto para importação no formato do Layout correto
            FileManager.save(
                    System.getProperty("user.home") + "/Desktop/FAPERS_BVPREV_import.csv",
                    Layout.getLayoutOfMaps(imports)
            );
            
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, "Erro: " + e.getMessage(), "Erro!", JOptionPane.ERROR_MESSAGE);
        }                
    }
    
    public static File getFile(String name) throws Exception{
        JOptionPane.showMessageDialog(null, "Escolha o arquivo " + name + ":");
        File file = Selector.selectFile("", name + " - .CSV", ".csv");
        if(file == null || Selector.verifyFile(file.getPath(), true, ".csv")){
            return file;
        }else{
            throw new Exception("O arquivo "+ name  +" não é válido");
        }
    }

}
