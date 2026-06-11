# Começando com Git Flow

Instruções passo a passo para começar a trabalhar com o projeto.

## Status Atual

Você tem 3 branches:
- `main` - produção
- `release` - preparação de versão
- `develop` - desenvolvimento

## 1. Setup Inicial (executar uma vez)

Clonar/atualizar o projeto:

```bash
cd ~/Documentos/workspace/todolist-api
git fetch origin
git branch -a
```

Você deve ver:
```
* main
  remotes/origin/develop
  remotes/origin/main
  remotes/origin/release
```

## 2. Criar a Primeira Feature

Criar branch feature a partir de develop:

```bash
git checkout develop
git pull origin develop
git checkout -b feature/initial-setup
```

## 3. Fazer Commits Seguindo o Padrão

Conforme você trabalha:

```bash
git add src/
git commit -m "feat: reorganizar estrutura de pastas

- Mudança de Clean Architecture para estrutura simplificada
- Novo layout: entity, repository, service, controller, request, response, config
- Mais intuitivo e fácil de manter"
```

ou

```bash
git commit -m "fix: corrigir validação de tarefa"
git commit -m "refactor: simplificar TaskService"
git commit -m "docs: atualizar README com novo layout"
git commit -m "chore: atualizar dependências Maven"
```

## 4. Enviar a Feature para Remoto

Quando terminar:

```bash
git push origin feature/initial-setup
```

## 5. Criar Pull Request no GitHub

1. Abra o repositório no GitHub
2. Clique em "Pull Requests"
3. Clique em "New Pull Request"
4. Configure:
   - Base: `develop`
   - Compare: `feature/initial-setup`
5. Adicione uma descrição:

```
## Descrição
Reorganização da estrutura de pastas para layout mais simples e intuitivo.

## Mudanças
- [x] Remover Clean Architecture (domain, application, infrastructure, presentation)
- [x] Criar nova estrutura (entity, repository, service, controller, request, response, config)
- [x] Atualizar imports de todas as classes
- [x] Adicionar documentação (ESTRUTURA.md, GITFLOW.md)

## Tipo
- [x] Feature
- [ ] Bug fix
- [ ] Breaking change

## Checklist
- [x] Código testado
- [x] README atualizado
- [x] Sem warnings
```

6. Clique "Create pull request"

## 6. Merge para Develop

Depois que a PR for aprovada/revisada:

1. GitHub: Clique "Merge pull request"
2. Confirme o merge
3. Delete a branch remota (opção que aparece)

Localmente:

```bash
git checkout develop
git pull origin develop
git branch -d feature/initial-setup
git branch -a
```

## 7. Próximas Features

Repita o processo para cada funcionalidade:

```bash
git checkout develop
git pull origin develop
git checkout -b feature/nova-feature
# ... trabalhe na feature ...
git push origin feature/nova-feature
# ... abra PR, merge ...
```

## Exemplos de Nomes de Features

```bash
feature/add-task-filters
feature/improve-error-handling
feature/add-pagination
feature/refactor-controllers
feature/update-dependencies
```

## Padrão de Commit

Sempre comece com:
- `feat:` - Nova funcionalidade
- `fix:` - Bug
- `refactor:` - Refatoração
- `docs:` - Documentação
- `chore:` - Tarefas gerais

Exemplos:

```bash
git commit -m "feat: adicionar filtro por data"
git commit -m "fix: resolver timeout de conexão"
git commit -m "refactor: extrair validação para método separado"
git commit -m "docs: adicionar exemplo de uso"
git commit -m "chore: atualizar versão do Spring Boot"
```

## Quando Fazer Release

Quando pronto para versão (exemplo v1.1.0):

```bash
git checkout develop
git pull origin develop
git checkout -b release/v1.1.0
git push origin release/v1.1.0
```

No GitHub:
1. PR: release/v1.1.0 → main
2. Merge
3. Tag com v1.1.0
4. PR: main → develop
5. Merge

## Comando Rápido: Ver Status

```bash
git status
git branch -a
git log --oneline -5
```

## Dúvidas?

Veja GITFLOW.md para detalhes completos do workflow.
