# Git Flow Workflow

Guia para trabalhar com Git Flow neste projeto.

## Branches Permanentes

- **main**: código em produção (tagged com versões v1.0.0, v1.1.0, etc)
- **develop**: código de desenvolvimento integrado

## Branches Temporárias

### Feature Branches
Para novas funcionalidades:

```bash
git checkout develop
git pull origin develop
git checkout -b feature/nome-funcionalidade
```

Commit pattern:
```bash
git commit -m "feat: descrição curta da funcionalidade"
git commit -m "fix: descrição do bug fix"
git commit -m "refactor: descrição da refatoração"
git commit -m "docs: descrição da documentação"
git commit -m "chore: descrição da tarefa"
```

Exemplos:
```bash
git commit -m "feat: adicionar filtro de tarefas por status"
git commit -m "fix: corrigir validação de data"
git commit -m "refactor: simplificar TaskService"
git commit -m "docs: atualizar README"
git commit -m "chore: atualizar dependências Maven"
```

### Merge para Develop

Quando a feature está pronta:

```bash
git push origin feature/nome-funcionalidade
```

Criar Pull Request no GitHub:
- Base: `develop`
- Compare: `feature/nome-funcionalidade`
- Descrever as mudanças
- Pedir review
- Merge

Após merge:

```bash
git checkout develop
git pull origin develop
git branch -d feature/nome-funcionalidade
git push origin --delete feature/nome-funcionalidade
```

### Release Branches

Quando pronto para liberar (exemplo v1.1.0):

```bash
git checkout develop
git pull origin develop
git checkout -b release/v1.1.0
```

Fazer ajustes e commits:
```bash
git commit -m "chore: bump version to 1.1.0"
```

Merge para main:

```bash
git push origin release/v1.1.0
```

No GitHub:
1. Create Pull Request: release/v1.1.0 → main
2. Merge
3. Create tag: v1.1.0
4. Create Pull Request: main → develop (para sincronizar)
5. Merge

## Workflow Resumido

1. **Criar feature a partir de develop**:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/sua-feature
   ```

2. **Trabalhar na feature**:
   ```bash
   git add .
   git commit -m "feat/fix/refactor/docs/chore: descrição"
   git push origin feature/sua-feature
   ```

3. **Pull Request para develop**:
   - Abrir PR no GitHub
   - Descrever mudanças
   - Pedir review
   - Merge quando aprovado

4. **Release (versão)**:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b release/vX.X.X
   ```

5. **Merge release para main**:
   - PR: release/vX.X.X → main
   - Tag com versão
   - PR: main → develop

## Convenção de Mensagens de Commit

- `feat:` - Nova funcionalidade
- `fix:` - Bug fix
- `docs:` - Documentação
- `style:` - Formatação, sem mudança de código
- `refactor:` - Refatoração
- `perf:` - Melhoria de performance
- `test:` - Testes
- `chore:` - Tarefas de build, dependências, etc

Exemplo:
```
feat: adicionar endpoint de filtro por data

Adiciona novo query param `date` no GET /api/v1/tasks
que permite filtrar tarefas por data de criação.

Fecha #123
```

## Branches Atuais

```
main
├── release (preparação para versão)
└── develop (integração de features)
```

## Próximos Passos

1. Criar feature branches a partir de develop
2. Fazer commits seguindo o padrão
3. Abrir PRs para develop
4. Quando pronto, criar release branch
5. Fazer PR release → main com tag de versão
