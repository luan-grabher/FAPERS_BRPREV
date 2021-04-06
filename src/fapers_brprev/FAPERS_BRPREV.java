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
    public static Integer month = 1;
    public static Integer year = 2021;

    public static void main(String[] args) {
        try {

            //Arquivo de contas
            File accountsFile = new File("./FAPERS_Accounts.csv");
            
            //Cria mapa de filtros de contas e historicos padroes
            Accounts.addOnList(accountsFile);

            //Mapa com importações em String para fazer
            List<Map<String, String>> imports = new ArrayList<>();
            
            //Pega lctos para importyação do banco
            imports.addAll(null);

            //Salva o arquivo de texto para importação no formato do Layout correto
            String saveFilePath = System.getProperty("user.home") + "/Desktop/FAPERS_BVPREV_import.csv";
            if (FileManager.save(
                    saveFilePath,
                    Layout.getLayoutOfMaps(imports)
            )) {
                JOptionPane.showMessageDialog(null, "Arquivo salvo em: \n" + saveFilePath);
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo final!");
            };

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro!", JOptionPane.ERROR_MESSAGE);
        }

        System.exit(0);
    }

    /**
     * Solicita arquivo CSV para o usuário e diz que o arquivo não é valido caso
     * não exista ou o usuário não escolha.
     *
     * @param name Nome do arquivo sem o '.csv'
     * @return O arquivo escolhido
     * @throws java.lang.Exception Causa um erro dizendo que o arquivo nao é
     * valido
     */
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
