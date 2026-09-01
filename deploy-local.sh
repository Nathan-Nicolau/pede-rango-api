#!/usr/bin/env bash
set -e

# ==============================================================================
# Script de Deploy Local - Food Delivery API (Spring Boot + Postgres)
# ==============================================================================

# Cores para saída no terminal
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configurações do ambiente
CLUSTER_NAME="meu-cluster-local"
RELEASE_NAME="minha-api-delivery-kubernetes"
CHART_DIR="./k8s-chart"
IMAGE_NAME="food-delivery-api"
DEFAULT_TAG="v2"

# Diretório base do script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Função para exibir ajuda
show_help() {
  echo -e "${BLUE}Uso:${NC} ./deploy-local.sh [OPÇÃO | TAG]"
  echo ""
  echo -e "${YELLOW}Opções principais:${NC}"
  echo "  (sem argumentos)   Executa o ciclo completo (build, carga no Kind e deploy) usando a tag padrão '${DEFAULT_TAG}'"
  echo "  <TAG>              Executa o ciclo completo com uma tag específica (ex: ./deploy-local.sh v3)"
  echo ""
  echo -e "${YELLOW}Subcomandos utilitários:${NC}"
  echo "  status             Exibe o status dos pods, services, ingress e HPA da API"
  echo "  logs               Acompanha os logs em tempo real dos pods da API"
  echo "  restart            Reinicia o deployment da API forçadamente"
  echo "  uninstall          Remove o release Helm do cluster"
  echo "  help | -h | --help Exibe esta mensagem de ajuda"
}

# Tratamento de subcomandos
case "$1" in
  help|-h|--help)
    show_help
    exit 0
    ;;
  status)
    echo -e "${BLUE}==> Verificando status da API no cluster Kind...${NC}"
    kubectl get pods,svc,ingress,hpa,statefulset -l "app in (food-delivery-api, food-delivery-db)"
    exit 0
    ;;
  logs)
    echo -e "${BLUE}==> Acompanhando logs da API (Ctrl+C para sair)...${NC}"
    kubectl logs -f -l app=food-delivery-api --all-containers=true --tail=50
    exit 0
    ;;
  restart)
    echo -e "${BLUE}==> Reiniciando o deployment da API...${NC}"
    kubectl rollout restart deployment/food-delivery-api
    kubectl rollout status deployment/food-delivery-api --timeout=120s
    exit 0
    ;;
  uninstall)
    echo -e "${YELLOW}==> Desinstalando release Helm '${RELEASE_NAME}'...${NC}"
    helm uninstall "$RELEASE_NAME"
    exit 0
    ;;
esac

TAG="${1:-$DEFAULT_TAG}"
TAR_FILE="${IMAGE_NAME}-${TAG}.tar"

# Garante a limpeza do arquivo .tar mesmo em caso de erro ou interrupção
cleanup() {
  if [ -f "$TAR_FILE" ]; then
    echo -e "${YELLOW}==> Limpando arquivo temporário ${TAR_FILE}...${NC}"
    rm -f "$TAR_FILE"
  fi
}
trap cleanup EXIT

echo -e "${BLUE}================================================================${NC}"
echo -e "${BLUE} Iniciando Build & Deploy: ${GREEN}${IMAGE_NAME}:${TAG}${NC}"
echo -e "${BLUE} Cluster Kind: ${YELLOW}${CLUSTER_NAME}${NC}"
echo -e "${BLUE} Release Helm: ${YELLOW}${RELEASE_NAME}${NC}"
echo -e "${BLUE}================================================================${NC}"

# 1. Build da Imagem Docker
echo -e "\n${BLUE}[1/4] Gerando imagem Docker (${IMAGE_NAME}:${TAG})...${NC}"
docker build -t "${IMAGE_NAME}:${TAG}" .

# 2. Exportando e Carregando no Kind
echo -e "\n${BLUE}[2/4] Exportando imagem para carga no Kind...${NC}"
docker save "${IMAGE_NAME}:${TAG}" -o "$TAR_FILE"

echo -e "${BLUE}      Carregando ${TAR_FILE} no cluster Kind '${CLUSTER_NAME}'...${NC}"
kind load image-archive "$TAR_FILE" --name "$CLUSTER_NAME"

# 3. Deploy / Upgrade via Helm
echo -e "\n${BLUE}[3/4] Aplicando upgrade/install no Helm com a tag '${TAG}'...${NC}"
helm upgrade --install "$RELEASE_NAME" "$CHART_DIR" \
  --set api.image="$IMAGE_NAME" \
  --set api.tag="$TAG"

# 4. Verificação de Saúde do Rollout
echo -e "\n${BLUE}[4/4] Aguardando inicialização completa da API...${NC}"
kubectl rollout status deployment/food-delivery-api --timeout=120s

echo -e "\n${GREEN}✔ Deploy da API concluído com sucesso!${NC}"
echo -e "Endpoint local: ${YELLOW}http://api.food-delivery.local${NC}"
