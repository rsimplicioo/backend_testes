# language: pt

Funcionalidade: Mensagem

  @smoke @high
  Cenario: : Registrar Mensagem
    Quando registrar uma nova mensagem
    Entao a mansagem é registrada com sucesso
    E deve ser apresentada

  @smoke @high
  Cenario: Buscar mensagem
    Dado que uma mensagem já foi publicada
    Quando efetuar a busca da mensagem
    Entao a mensagem é exibida com sucesso

  @low
  Cenario: Alterar mensagem
    Dado que uma mensagem já foi publicada
    Quando efetuar requisição para alterar mensagem
    Entao a mensagem é atualizada com sucesso
    E deve ser apresentada

  @high
  Cenario: Remover mensagem
    Dado que uma mensagem já foi publicada
    Quando requisitar a remoção da mensagem
    Entao a mensagem é excluída com sucesso

  @ignore
  Cenario: Remover mensagem de uma forma diferente
    Dado que uma mensagem já foi publicada
    Quando requisitar a remoção da mensagem
    Entao a mensagem é excluída com sucesso
