package fapers_brprev;

import fapers_brprev.Model.Accounts;
import fapers_brprev.Model.Layout;
import fapers_brprev.Model.PayRoll;
import fileManager.FileManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class FAPERS_BRPREV {
    public static Integer month = 1;
    public static Integer year = 2021;
    public static StringBuilder log = new StringBuilder();

    public static void main(String[] args) {
        try {

            //Arquivo de contas
            File accountsFile = new File("./FAPERS_Accounts.csv");
            
            if(accountsFile.exists()){
                //Cria mapa de filtros de contas e historicos padroes
                Accounts.addOnList(accountsFile);
                
                //Pega mes e ano
                month = Integer.valueOf(JOptionPane.showInputDialog("Insira o mês:"));
                year = Integer.valueOf(JOptionPane.showInputDialog("Insira o ano:"));

                //Mapa com importações em String para fazer
                List<Map<String, String>> imports = new ArrayList<>();

                //Pega lctos para importyação do banco
                imports.addAll(PayRoll.getImports());

                //Salva o arquivo de texto para importação no formato do Layout correto
                File desktopPath = new File(System.getProperty("user.home") + "/Desktop/");
                if (FileManager.save(
                        desktopPath,
                        "FAPERS_BVPREV_import.csv",
                        Layout.getLayoutOfMaps(imports)
                )) {
                    if(!"".equals(log.toString())){
                        FileManager.save(accountsFile, "LOG_FAPERS.txt", log.toString());
                        JOptionPane.showMessageDialog(null, "Arquivo de Log salvo na área de trabalho");
                    }
                    
                    JOptionPane.showMessageDialog(null, "Arquivo layout de importação salvo na área de trabalho");
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo final!");
                };
            }else{
                throw new Exception("O arquivo 'FAPERS_Accounts.csv' não existe na pasta do programa!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro!", JOptionPane.ERROR_MESSAGE);
        }

        System.exit(0);
    }    

}
