package websocket;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import modelos.Comentario;

@ApplicationScoped
public class ManipuladorSessao {
    private final ConcurrentMap<String, ClienteConectado> sessoes = new ConcurrentHashMap<String, ClienteConectado>();
    private final LinkedList<Comentario> comentarios = new LinkedList<Comentario>();

    public void adicionarSessao(Session s){
        sessoes.put(s.getId(), new ClienteConectado(s));
        System.out.println("Abertura de conexao: id=" + s.getId() + ", total=" + sessoes.size());
        enviarHistorico(s);
    }

    public void identificarCliente(Session s, String nome){
        ClienteConectado cliente = sessoes.get(s.getId());
        if(cliente == null){
            return;
        }

        cliente.setNome(nome);
        JsonObject jo = criarJsonBase("clienteEntrou")
                .add("clienteId", s.getId())
                .add("nome", cliente.getNome())
                .add("totalClientes", sessoes.size())
                .build();
        enviarParaTodos(jo);
    }

    public void removerSessao(Session s){
        ClienteConectado removido = sessoes.remove(s.getId());
        String nome = removido == null ? "Cliente " + s.getId() : removido.getNome();
        System.out.println("Encerramento de conexao: id=" + s.getId() + ", total=" + sessoes.size());

        JsonObject jo = criarJsonBase("clienteSaiu")
                .add("clienteId", s.getId())
                .add("nome", nome)
                .add("totalClientes", sessoes.size())
                .build();
        enviarParaTodos(jo);
    }

    public void adicionarComentario(Comentario c, Session origem){
        ClienteConectado cliente = sessoes.get(origem.getId());
        String nome = cliente == null ? "Cliente " + origem.getId() : cliente.getNome();

        comentarios.add(c);
        System.out.println("Recebimento de mensagem: id=" + origem.getId() + ", nome=" + nome + ", texto=" + c.getDescricao());

        JsonObject jo = criarJsonBase("comentarioAdicionado")
                .add("clienteId", origem.getId())
                .add("nome", nome)
                .add("descricao", c.getDescricao())
                .add("totalClientes", sessoes.size())
                .build();
        enviarParaTodos(jo);
    }

    public void adicionarComentario(Comentario c){
        comentarios.add(c);
        JsonObject jo = criarJsonBase("comentarioAdicionado")
                .add("clienteId", "servidor")
                .add("nome", "Servidor")
                .add("descricao", c.getDescricao())
                .add("totalClientes", sessoes.size())
                .build();
        enviarParaTodos(jo);
    }

    public void removerComentario(String descricao){}

    private void enviarHistorico(Session s){
        for(int i = 0; i < comentarios.size(); i++){
            JsonObject jo = criarJsonBase("comentarioAdicionado")
                    .add("clienteId", "historico")
                    .add("nome", "Historico")
                    .add("descricao", comentarios.get(i).getDescricao())
                    .add("totalClientes", sessoes.size())
                    .build();
            try {
                s.getBasicRemote().sendText(jo.toString());
            } catch (IOException ex) {
                sessoes.remove(s.getId());
                ex.printStackTrace();
            }
        }
    }

    private void enviarParaTodos(JsonObject jo){
        Collection<ClienteConectado> clientes = sessoes.values();
        for(ClienteConectado cliente : clientes){
            Session sessao = cliente.getSessao();
            if(!sessao.isOpen()){
                sessoes.remove(sessao.getId());
                continue;
            }

            try {
                sessao.getBasicRemote().sendText(jo.toString());
            } catch (IOException ex) {
                sessoes.remove(sessao.getId());
                ex.printStackTrace();
            }
        }
    }

    private javax.json.JsonObjectBuilder criarJsonBase(String acao){
        JsonProvider jp  = JsonProvider.provider();
        return jp.createObjectBuilder().add("acao", acao);
    }

    private static class ClienteConectado {
        private final Session sessao;
        private String nome;

        ClienteConectado(Session sessao) {
            this.sessao = sessao;
            this.nome = "Cliente " + sessao.getId();
        }

        Session getSessao() {
            return sessao;
        }

        String getNome() {
            return nome;
        }

        void setNome(String nome) {
            if(nome == null || nome.trim().isEmpty()){
                return;
            }
            this.nome = nome.trim();
        }
    }
}
