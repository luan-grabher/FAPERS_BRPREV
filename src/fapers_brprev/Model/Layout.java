package fapers_brprev.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Layout {

    /**
     * Coloca um texto à esquerda de uma string passada completando até ter o
     * tamanho desejado
     *
     * @param inputString A string inicial que deverá possuir o tamanho correto
     * @param append O que irá colocar para completar o tamanho string
     * @param length Tamanho que a string deve ter
     * @return A string passada com o tamanho definido e preenchido com o valor
     * definido caso precise
     */
    public static String padLeft(String inputString, String append, int length) {
        //Se a String ja tiver o tamanho
        if (inputString.length() == length) {
            return inputString;
        } //Se a String for maior que o tamanho
        else if (inputString.length() > length) {
            //Retorna da direita para a esquerda
            return inputString.substring(inputString.length() - 1 - length, inputString.length() - 1);
        } //Se for menor
        else {
            StringBuilder sb = new StringBuilder();
            while (sb.length() < length - inputString.length()) {
                sb.append(append);
            }
            sb.append(inputString);

            return sb.toString();
        }
    }

    /**
     * Coloca um texto à direita de uma string passada completando até ter o
     * tamanho desejado
     *
     * @param inputString A string inicial que deverá possuir o tamanho correto
     * @param append O que irá colocar para completar o tamanho string
     * @param length Tamanho que a string deve ter
     * @return A string passada com o tamanho definido e preenchido com o valor
     * definido caso precise
     */
    public static String padRight(String inputString, String append, int length) {
        //Se a String ja tiver o tamanho
        if (inputString.length() == length) {
            return inputString;
        } //Se a String for maior que o tamanho
        else if (inputString.length() > length) {
            //Retorna da esquerda para a direita
            return inputString.substring(0, length - 1);
        } //Se for menor
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(inputString);

            while (sb.length() < length) {
                sb.append(append);
            }

            return sb.toString();
        }
    }

    /**
     * Converte uma lista de mapa de lançamentos para o formato do layout de
     * importacao de texto
     *
     * @param maps Lista com mapas de lctos
     * @return String completa para importação
     */
    public static String getLayoutOfMaps(List<Map<String, String>> maps) {
        StringBuilder sb = new StringBuilder();

        maps.forEach((m) -> {
            if (!"".equals(sb.toString())) {
                sb.append("\r\n");
            }

            sb.append(createStringWithMap(m));
        });

        return sb.toString();
    }

    /**
     * Retorna o mapa padrão com os valores padrões
     *
     * @return Retorna mapa com puts padrões
     */
    public static Map<String, String> getDefaultMap() {
        Map<String, String> map = new HashMap<>();

        map.put("empresa", "1");
        map.put("plano", "998");
        map.put("perfil", "");
        map.put("dataCD", "");
        map.put("numeroCD", "123");
        map.put("sequencial", "");
        map.put("sequencialContrapartida", "");
        map.put("conta", "");
        map.put("auxiliar", "");
        map.put("centroCusto", "0");
        map.put("centroCusteio", "9900000000");
        map.put("documento", "");
        map.put("numeroDocumento", "0");
        map.put("dataDocumento", "");
        map.put("valorLançamento", ""); //O tamanho correto é 15, mas o primeiro será 0 ou o sinal de menos
        map.put("indicadorDebitoCredito", "D");
        map.put("historicoPadrao", "202");
        map.put("descricaoHistorico", "");
        map.put("observacao", "MORESCO CONTABILIDADE FOLHA");

        return map;
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
        sb.append(getMapStrPadRight(map, "conta", 21));
        sb.append(getMapStrPadRight(map, "auxiliar", 14, " "));
        sb.append(getMapStrPadRight(map, "centroCusto", 10, " "));
        sb.append(getMapStrPadRight(map, "centroCusteio", 10, " "));
        sb.append(getMapStrPadRight(map, "documento", 15, " "));
        sb.append(getMapStrPadRight(map, "numeroDocumento", 5, " "));
        sb.append(getMapStrPadRight(map, "dataDocumento", 8));
        sb.append(map.getOrDefault("indicadorDebitoCredito", "D").equals("C") ? "-" : "0"); //Sinal ou zero dependendo do indicador de debito/credito
        sb.append(getMapStrPadLeft(map, "valorLançamento", 14)); //O tamanho correto é 15, mas o primeiro será 0 ou o sinal de menos
        sb.append(getMapStrPadRight(map, "indicadorDebitoCredito", 1, "D"));
        sb.append(getMapStrPadRight(map, "historicoPadrao", 5, " "));
        sb.append(getMapStrPadRight(map, "descricaoHistorico", 300, " "));
        sb.append(getMapStrPadRight(map, "observacao", 200, " "));

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
        return getMapStrPadLeft(map, str, size, "0");
    }

    /**
     * Pega String do mapa com tamanho correto e preenche a esquerda
     */
    private static String getMapStrPadLeft(Map<String, String> map, String str, Integer size, String left) {
        return padLeft(map.getOrDefault(str, ""), left, size);
    }
}
