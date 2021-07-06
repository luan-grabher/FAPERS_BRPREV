package fapers_brprev;

import fapers_brprev.Model.Accounts;
import fapers_brprev.Model.Layout;
import fapers_brprev.Model.PayRoll;
import fileManager.FileManager;
import java.awt.Desktop;
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
            File accountsFile = FileManager.getFile("./FAPERS_Accounts.csv");
            File hpFile = FileManager.getFile("./FAPERS_HP.csv");

            //Cria mapa de filtros de contas e historicos padroes
            Accounts.createAccountsMap(accountsFile);
            Accounts.createHpMap(hpFile);

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
                Accounts.printNotFindInLog();
                if (!"".equals(log.toString())) {
                    FileManager.save(desktopPath, "LOG_FAPERS.txt", log.toString());
                    JOptionPane.showMessageDialog(
                            null,
                            "Alguns lançamentos não foram para o arquivo de layout porque não foram encontrados debito, credito ou historico nos arquivos de DE_PARA."
                            + "\nSalvei um log na sua area de trabalho informando quais não foram importados."
                            + "\nEstou abrindo para você completar o arquivo de contas e de historicos."
                            + "\nPreencha o campo de histórico com os termos que o historico deve ter separados por espaços."
                            + "\nPreencha as contas da FAPERS e a correspondente do UNICO, o nome da conta não é obrigatório, é apenas para identificação."
                    );

                    //Abre o arquivo de contas para a pessoa completar
                    Desktop.getDesktop().open(accountsFile);
                    Desktop.getDesktop().open(hpFile);
                    Desktop.getDesktop().open(new File(desktopPath + "/LOG_FAPERS.txt"));
                }

                JOptionPane.showMessageDialog(null, "Arquivo layout de importação salvo na área de trabalho");
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo final!");
            };
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro!", JOptionPane.ERROR_MESSAGE);
        }

        System.exit(0);
    }

}
