package fapers_brprev;

import fapers_brprev.Model.Accounts;
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
            //File payRoll = getFile("Relatório Espelho Resumo Folha de pagamento CSV");
            File payRoll = new File("D:\\NetBeansProjects\\FAPERS_BRPREV\\RELATORIO_ESPELHO_RESUMO.csv");

            //Arquivo de contas
            File accountsFile = getFile("Arquivo CSV com contas e historicos padrões");
            //Cria mapa de filtros de contas e historicos padroes
            Accounts.addOnList(accountsFile);
            
            //File file3 = getFile("3");
            //Mapa com importações em String para fazer
            List<Map<String, String>> imports = new ArrayList<>();

            //Extrai com o modelo dos arquivos as importações
            imports.addAll(PayRoll.getImports(payRoll));
            //imports.addAll(null);
            //imports.addAll(null);

            //Salva o arquivo de texto para importação no formato do Layout correto
            String saveFilePath = System.getProperty("user.home") + "/Desktop/FAPERS_BVPREV_import.csv";
            if (FileManager.save(
                    saveFilePath,
                    Layout.getLayoutOfMaps(imports)
            )) {
                JOptionPane.showMessageDialog(null, "Arquivo salvo em: \n" + saveFilePath);
            }else{
                JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo final!");
            };

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro!", JOptionPane.ERROR_MESSAGE);
        }
        
        System.exit(0);
    }

    public static File getFile(String name) throws Exception {
        JOptionPane.showMessageDialog(null, "Escolha o arquivo " + name + ":");
        File file = Selector.selectFile("", name + " - .CSV", ".csv");
        if (file == null || Selector.verifyFile(file.getPath(), true, ".csv")) {
            return file;
        } else {
            throw new Exception("O arquivo " + name + " não é válido");
        }
    }

}
