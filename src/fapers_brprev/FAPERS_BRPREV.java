package fapers_brprev;

import fapers_brprev.Model.Accounts;
import fapers_brprev.Model.Layout;
import fapers_brprev.Model.PayRoll;
import fileManager.FileManager;
import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class FAPERS_BRPREV {

    public static Integer month = 6;
    public static Integer year = 2023;
    public static File unicoFolhaTxt;

    public static void main(String[] args) {
        try {

            //Arquivo de contas
            File accountsFile = FileManager.getFile("./FAPERS_Accounts.csv");
            File hpFile = FileManager.getFile("./FAPERS_HP.csv");

            //Cria mapa de filtros de contas e historicos padroes
            Accounts.createAccountsMap(accountsFile);
            Accounts.createHpMap(hpFile);

            Integer yearNow = Calendar.getInstance().get(Calendar.YEAR);

            Integer[] months = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            Integer[] years = new Integer[]{yearNow, yearNow - 1, yearNow - 2};

            //Pega mes e ano
            month = (Integer) JOptionPane.showInputDialog(null, "Insira o mês:", "Insira o MÊS", JOptionPane.QUESTION_MESSAGE, null, months, months[0]);
            year = (Integer) JOptionPane.showInputDialog(null, "Insira o ano:", "Insira o ANO", JOptionPane.QUESTION_MESSAGE, null, years, years[0]);

            unicoFolhaTxt = FileManager.getFileFromUser("Arquivo de Lançamentos contábeis do UNICO", "txt");
            if (unicoFolhaTxt == null) {
                throw new Exception("Arquivo de lançamentos do UNICO não selecionado.");
            }         

            //Mapa com importações em String para fazer
            List<Map<String, String>> imports = new ArrayList<>();
            imports.addAll(PayRoll.getImports());

            if (imports.isEmpty()) {
                throw new Exception("Não há lançamentos para importar identificados no arquivo de lançamentos do UNICO.");
            }

            Boolean hasAccountsNotFind = Accounts.notFind.size() > 0;
            if (hasAccountsNotFind){
                Accounts.notFindToFiles(accountsFile, hpFile);

                JOptionPane.showMessageDialog(
                        null,
                        "Alguns lançamentos não foram para o arquivo de layout porque não foram encontrados debito, credito ou historico nos arquivos de DE_PARA."
                        + "\nEstou abrindo para você completar o arquivo de contas e de historicos."
                        + "\nComplete as colunas que estiverem em branco."
                        + "\nPreencha o campo de histórico com os termos que o historico deve ter separados por espaços."
                        + "\nPreencha as contas da FAPERS e a correspondente do UNICO, o nome da conta não é obrigatório, é apenas para identificação."
                );

                //Abre o arquivo de contas para a pessoa completar
                Desktop.getDesktop().open(accountsFile);
                Desktop.getDesktop().open(hpFile);

                return;
            }

            //Salva o arquivo de texto para importação no formato do Layout correto
            File desktopPath = new File(System.getProperty("user.home") + "/Desktop/");
            Boolean isErrorOnSaveDesktop = !FileManager.save(
                    desktopPath,
                    "FAPERS_BVPREV_import " + year + "_" + month + ".txt",
                    Layout.getLayoutOfMaps(imports)
            );
            if (isErrorOnSaveDesktop) {
                throw new Exception("Erro ao salvar arquivo de importação na área de trabalho.");
            }

            JOptionPane.showMessageDialog(null, "Arquivo layout de importação salvo na área de trabalho");           
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro!", JOptionPane.ERROR_MESSAGE);
        } catch (Error e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro!", JOptionPane.ERROR_MESSAGE);
        }

        System.exit(0);
    }

}
