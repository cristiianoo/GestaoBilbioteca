package gui;

import modelo.Livro;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class GestaoLivros extends javax.swing.JFrame {
    
    private ArrayList<Livro> listaLivros;
    private final String ARQUIVO_CSV = "livros.csv";
    private DefaultTableModel modeloTabela;
    private int proximoId = 1;
    
    /**
     * Creates new form GestaoLivros
     */
    public GestaoLivros() {
        initComponents();
        configurarTabela();
        listaLivros = new ArrayList<>();
        carregarDadosDoCSV();
        atualizarTabela();
        
        // Configurar ações dos botões
        configurarAcoesBotoes();
        
        // Configurar o ComboBox com opções de busca mais claras
        configurarComboBox();
        
        // Configurar o campo de filtro com dica ao usuário
        jTextField1.setToolTipText("Digite o termo para busca");
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (jTextField1.getText().equals("Filtro")) {
                    jTextField1.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (jTextField1.getText().isEmpty()) {
                    jTextField1.setText("Filtro");
                }
            }
        });
    }
    
    private void configurarComboBox() {
        // Limpar itens atuais
        jComboBox1.removeAllItems();
        
        // Adicionar opções com nomes claros
        jComboBox1.addItem("Título");
        jComboBox1.addItem("ISBN");
        jComboBox1.addItem("Autor");
        jComboBox1.addItem("Estado");
        jComboBox1.addItem("Todos");
        
        // Selecionar a primeira opção por padrão
        jComboBox1.setSelectedIndex(0);
    }
    
    private void configurarTabela() {
        // Configurar o modelo da tabela
        modeloTabela = new DefaultTableModel(
            new Object [][] {},
            new String [] {"Título", "ISBN", "Autor", "Estado"}
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        };
        
        jTable1.setModel(modeloTabela);
    }
    
    private void configurarAcoesBotoes() {
        // Ação para o botão Adicionar
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adicionarLivro();
            }
        });

        // Ação para o botão Remover
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removerLivroSelecionado();
            }
        });

        // Ação para o botão Editar
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarLivroSelecionado();
            }
        });

        // Ação para o botão Procurar
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtrarLivros();
            }
        });
    }

    private void adicionarLivro() {
        // Obter os valores dos campos
        String isbn = jTextField2.getText().trim();
        String titulo = jTextField3.getText().trim();
        String autor = jTextField4.getText().trim();
        
        // Validar os campos
        if (isbn.isEmpty() || titulo.isEmpty() || autor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar se o ISBN já existe
        for (Livro livro : listaLivros) {
            if (livro.getIsbn().equals(isbn)) {
                JOptionPane.showMessageDialog(this, "ISBN já cadastrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Criar e adicionar o novo livro
        Livro novoLivro = new Livro(proximoId, isbn, titulo, autor);
        proximoId++;
        listaLivros.add(novoLivro);
        
        // Salvar os dados no CSV
        salvarDadosNoCSV();
        
        // Atualizar a tabela
        atualizarTabela();
        
        // Limpar os campos
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        
        // Fechar o diálogo
        jDialog1.dispose();
        
        JOptionPane.showMessageDialog(this, "Livro adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void removerLivroSelecionado() {
        int linhaSelecionada = jTable1.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro para remover!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacao = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja remover este livro?", 
                "Confirmação", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            listaLivros.remove(linhaSelecionada);
            salvarDadosNoCSV();
            atualizarTabela();
            JOptionPane.showMessageDialog(this, "Livro removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void editarLivroSelecionado() {
        int linhaSelecionada = jTable1.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro para editar!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Obter o livro selecionado
        Livro livroSelecionado = listaLivros.get(linhaSelecionada);
        
        // Preencher o formulário com os dados do livro
        jTextField2.setText(livroSelecionado.getIsbn());
        jTextField3.setText(livroSelecionado.getTitulo());
        jTextField4.setText(livroSelecionado.getAutor());
        
        // Mudar o título do botão para "Atualizar"
        jButton6.setText("Atualizar");
        
        // Mostrar o diálogo
        jDialog1.setVisible(true);
        
        // Configurar uma ação temporária para o botão "Atualizar"
        jButton6.removeActionListener(jButton6.getActionListeners()[0]);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Obter os valores dos campos
                String isbn = jTextField2.getText().trim();
                String titulo = jTextField3.getText().trim();
                String autor = jTextField4.getText().trim();
                
                // Validar os campos
                if (isbn.isEmpty() || titulo.isEmpty() || autor.isEmpty()) {
                    JOptionPane.showMessageDialog(GestaoLivros.this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Verificar se o ISBN já existe (exceto para o próprio livro)
                for (Livro livro : listaLivros) {
                    if (livro.getIsbn().equals(isbn) && livro != livroSelecionado) {
                        JOptionPane.showMessageDialog(GestaoLivros.this, "ISBN já cadastrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                // Atualizar os dados do livro
                livroSelecionado.setIsbn(isbn);
                livroSelecionado.setTitulo(titulo);
                livroSelecionado.setAutor(autor);
                
                // Salvar os dados no CSV
                salvarDadosNoCSV();
                
                // Atualizar a tabela
                atualizarTabela();
                
                // Limpar os campos
                jTextField2.setText("");
                jTextField3.setText("");
                jTextField4.setText("");
                
                // Fechar o diálogo
                jDialog1.dispose();
                
                // Restaurar a ação original do botão
                jButton6.removeActionListener(jButton6.getActionListeners()[0]);
                jButton6.setText("Adicionar");
                configurarAcoesBotoes();
                
                JOptionPane.showMessageDialog(GestaoLivros.this, "Livro atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    
    private void filtrarLivros() {
        String termoBusca = jTextField1.getText().trim().toLowerCase();
        String tipoBusca = (String) jComboBox1.getSelectedItem();
        
        // Se o campo de busca estiver vazio ou for o texto padrão, mostrar todos os livros
        if (termoBusca.isEmpty() || termoBusca.equalsIgnoreCase("filtro")) {
            atualizarTabela();
            return;
        }
        
        // Limpar a tabela
        modeloTabela.setRowCount(0);
        
        // Filtrar os livros com base no tipo de busca
        for (Livro livro : listaLivros) {
            boolean adicionarLivro = false;
            
            switch (tipoBusca) {
                case "Título":
                    adicionarLivro = livro.getTitulo().toLowerCase().contains(termoBusca);
                    break;
                case "ISBN":
                    adicionarLivro = livro.getIsbn().toLowerCase().contains(termoBusca);
                    break;
                case "Autor":
                    adicionarLivro = livro.getAutor().toLowerCase().contains(termoBusca);
                    break;
                case "Estado":
                    adicionarLivro = livro.getEstadoTexto().toLowerCase().contains(termoBusca);
                    break;
                case "Todos":
                default:
                    adicionarLivro = livro.getTitulo().toLowerCase().contains(termoBusca) ||
                                     livro.getIsbn().toLowerCase().contains(termoBusca) ||
                                     livro.getAutor().toLowerCase().contains(termoBusca) ||
                                     livro.getEstadoTexto().toLowerCase().contains(termoBusca);
                    break;
            }
            
            if (adicionarLivro) {
                modeloTabela.addRow(new Object[]{
                    livro.getTitulo(),
                    livro.getIsbn(),
                    livro.getAutor(),
                    livro.getEstadoTexto()
                });
            }
        }
    }
    
    private void atualizarTabela() {
        // Limpar a tabela
        modeloTabela.setRowCount(0);
        
        // Adicionar todos os livros na tabela
        for (Livro livro : listaLivros) {
            modeloTabela.addRow(new Object[]{
                livro.getTitulo(),
                livro.getIsbn(),
                livro.getAutor(),
                livro.getEstadoTexto()
            });
        }
    }
    
    private void carregarDadosDoCSV() {
        listaLivros.clear();
        
        try {
            File arquivo = new File(ARQUIVO_CSV);
            
            // Se o arquivo não existe, não precisa carregar nada
            if (!arquivo.exists()) {
                proximoId = 1;
                return;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(arquivo));
            String linha;
            
            // Ler a primeira linha para ignorar (cabeçalho)
            linha = reader.readLine();
            
            while ((linha = reader.readLine()) != null) {
                // Separar os valores por vírgula
                String[] valores = linha.split(",");
                
                // Verificar se tem todos os valores necessários
                if (valores.length >= 5) {
                    int id = Integer.parseInt(valores[0]);
                    String isbn = valores[1];
                    String titulo = valores[2];
                    String autor = valores[3];
                    boolean disponivel = Boolean.parseBoolean(valores[4]);
                    
                    // Criar o livro e adicionar na lista
                    Livro livro = new Livro(id, isbn, titulo, autor);
                    livro.setDisponivel(disponivel);
                    listaLivros.add(livro);
                    
                    // Atualizar o próximo ID
                    if (id >= proximoId) {
                        proximoId = id + 1;
                    }
                }
            }
            
            reader.close();
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                    "Erro ao carregar os dados: " + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void salvarDadosNoCSV() {
        try {
            FileWriter writer = new FileWriter(ARQUIVO_CSV);
            
            // Escrever o cabeçalho
            writer.write("ID,ISBN,Título,Autor,Disponível\n");
            
            // Escrever os dados de cada livro
            for (Livro livro : listaLivros) {
                writer.write(livro.getId() + "," +
                             livro.getIsbn() + "," +
                             livro.getTitulo() + "," +
                             livro.getAutor() + "," +
                             livro.isDisponivel() + "\n");
            }
            
            writer.close();
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                    "Erro ao salvar os dados: " + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton4 = new javax.swing.JButton();

        jDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jDialog1.setPreferredSize(new java.awt.Dimension(400, 300));
        jDialog1.setResizable(false);
        jDialog1.setSize(new java.awt.Dimension(400, 310));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Detalhes do Livro");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("ISBN");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Título");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Autor");

        jTextField2.setToolTipText("");

        jButton5.setBackground(new java.awt.Color(153, 0, 0));
        jButton5.setText("Cancelar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Adicionar");

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField2))
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 202, Short.MAX_VALUE))
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3))
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton5)))
                .addContainerGap())
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Titulo", "ISBN", "Autor", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
        }

        jButton1.setText("Adicionar Livro");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Remover Livro");

        jButton3.setText("Editar Livro");

        jTextField1.setText("Filtro");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Título", "ISBN", "Autor", "Estado", "Todos" }));

        jButton4.setText("Procurar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(35, 35, 35)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        jDialog1.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Limpar os campos
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        
        // Restaurar o texto do botão (caso tenha sido alterado)
        jButton6.setText("Adicionar");
        
        // Remover todos os action listeners atuais do botão e adicionar o original
        for (java.awt.event.ActionListener al : jButton6.getActionListeners()) {
            jButton6.removeActionListener(al);
        }
        
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adicionarLivro();
            }
        });
        
        // Mostrar o diálogo
        jDialog1.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GestaoLivros.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GestaoLivros.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GestaoLivros.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GestaoLivros.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GestaoLivros().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
}