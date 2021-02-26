package fapers_brprev.Model;

import java.util.List;
import java.util.Map;

public class Layout {

    /**
     * Coloca um texto à esquerda de uma string passada completando até ter o
     * tamanho desejado
     *
     * @param inputString A string inicial que deverá possuir o tamanho correto
     * @param left O que irá colocar para completar o tamanho string
     * @param length Tamanho que a string deve ter
     * @return A string passada com o tamanho definido e preenchido com o valor
     * definido caso precise
     */
    public static String padLeft(String inputString, String left, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append(left);
        }
        sb.append(inputString);

        return sb.toString();
    }

    /**
     * Coloca um texto à direita de uma string passada completando até ter o
     * tamanho desejado
     *
     * @param inputString A string inicial que deverá possuir o tamanho correto
     * @param right O que irá colocar para completar o tamanho string
     * @param length Tamanho que a string deve ter
     * @return A string passada com o tamanho definido e preenchido com o valor
     * definido caso precise
     */
    public static String padRight(String inputString, String right, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(inputString);

        while (sb.length() < length) {
            sb.append(right);
        }

        return sb.toString();
    }

    /**
     * Converte uma lista de mapa de lançamentos para o formato do layout de
     * importacao de texto
     * @param maps Lista com mapas de lctos
     * @return String completa para importação
     */
    public static String getLayoutOfMaps(List<Map<String, String>> maps) {
        StringBuilder sb = new StringBuilder();
        
        maps.forEach((m)->{
            if(!"".equals(sb.toString())){
                sb.append("\r\n");                
            }
            
            sb.append(createStringWithMap(m));
        });
        
        return sb.toString();
    }

    /**
     * Cria string final com o mapa
     * <p>
     * @param map Mapa com informações de uma linha que será inserida
     * @return String do mapa da linha para colocar no arquivo do layout
     */
    private static String createStringWithMap(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();

        if (!"".equals(sb.toString())) {
            sb.append("\r\n");
        }

        /**
         * Pega o vetor do mapa e coloca
         */
        sb.append(getMapStrPadLeft(map, "empresa", 5));
        sb.append(getMapStrPadLeft(map, "plano", 5));
        sb.append(getMapStrPadLeft(map, "perfil", 5));
        sb.append(getMapStrPadLeft(map, "dataCD", 8));
        sb.append(getMapStrPadLeft(map, "numeroCD", 5));
        sb.append(getMapStrPadLeft(map, "sequencial", 5));
        sb.append(getMapStrPadLeft(map, "sequencialContrapartida", 5));
        sb.append(getMapStrPadRight(map, "conta", 20));
        sb.append(getMapStrPadRight(map, "auxiliar", 15, ""));
        sb.append(getMapStrPadRight(map, "centroCusto", 10, ""));
        sb.append(getMapStrPadRight(map, "centroCusteio", 10, ""));
        sb.append(getMapStrPadRight(map, "documento", 15, ""));
        sb.append(getMapStrPadRight(map, "numeroDocumento", 5, ""));
        sb.append(getMapStrPadRight(map, "dataDocumento", 8));
        sb.append(map.getOrDefault("indicadorDebitoCredito", "D").equals("C") ? "-" : "0"); //Sinal ou zero dependendo do indicador de debito/credito
        sb.append(getMapStrPadLeft(map, "valorLançamento", 14)); //O tamanho correto é 15, mas o primeiro será 0 ou o sinal de menos
        sb.append(getMapStrPadRight(map, "indicadorDebitoCredito", 1, "D"));
        sb.append(getMapStrPadRight(map, "historicoPadrao", 5, ""));
        sb.append(getMapStrPadRight(map, "descricaoHistorico", 300, ""));
        sb.append(getMapStrPadRight(map, "observacao", 200, ""));

        return sb.toString();
    }

    /**
     * Pega String do mapa com tamanho corrto e preenche a direita com "0"
     */
    private static String getMapStrPadRight(Map<String, String> map, String str, Integer size) {
        return getMapStrPadRight(map, str, size, "0");
    }

    /**
     * Pega String do mapa com tamanho corrto e preenche a direita
     */
    private static String getMapStrPadRight(Map<String, String> map, String str, Integer size, String left) {
        return padRight(map.getOrDefault(str, ""), left, size);
    }

    /**
     * Pega String do mapa com tamanho corrto e preenche a esquerda com "0"
     */
    private static String getMapStrPadLeft(Map<String, String> map, String str, Integer size) {
        return getMapStrPadRight(map, str, size, "0");
    }

    /**
     * Pega String do mapa com tamanho corrto e preenche a esquerda
     */
    private static String getMapStrPadLeft(Map<String, String> map, String str, Integer size, String left) {
        return padRight(map.getOrDefault(str, ""), left, size);
    }
}
