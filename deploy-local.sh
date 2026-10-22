#!/usr/bin/env bash
set -e

CLUSTER_NAME="meu-cluster-local"
IMAGE_TAG="v1"
IMAGE_NAME="pede-rango-api:${IMAGE_TAG}"

echo "🚀 [1/4] Compilando e gerando imagem Docker '${IMAGE_NAME}'..."
docker build -t "${IMAGE_NAME}" .

echo "📦 [2/4] Carregando imagem no cluster Kind via ctr..."
docker save "${IMAGE_NAME}" | docker exec -i "${CLUSTER_NAME}-control-plane" ctr --namespace=k8s.io images import -

echo "🔄 [3/4] Reiniciando deployment 'pede-rango-api'..."
kubectl rollout restart deployment/pede-rango-api

echo "⏳ [4/4] Aguardando inicialização completa da API..."
kubectl rollout status deployment/pede-rango-api

echo "✔ API Backend atualizada com sucesso!"
