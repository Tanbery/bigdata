https://medium.com/@TheNileshkumar/setup-apache-nifi-cluster-in-kubernates-fce5dca0bdbc

1. Ensure you have kubectl in your local. Follow the kubectl installation steps.
2. Connect to your kubernates cluster.
3. Kubectl get ns
4. kubectl create namespace nifi
5. Use Namespace : `kubectl config set-context — current — namespace=nifi`
6. Ensure you have helm installed in your host machine. If not, follow the helm installation steps from their helm’s website.
7. Now pull the helm repo using following command :
8. helm repo add cetic https://cetic.github.io/helm-charts
9. helm repo update
10. helm install nifi cetic/nifi
11. To access nifi locally you can use following commands :
kubectl port-forward -n nifi svc/nifi 8443:8443

…and point your web browser to https://localhost:8443/nifi/

Credential : Username / changemechangeme

This will bring up one nifi-node in you kubernetes but not in cluster mode.

To run it in cluster node, update following properties in values.yaml file in the helm-chart.
` — -
# Number of nifi nodes
replicaCount: 3

properties:

isNode: true

certManager:
enabled: true

If you dont have CertManager, we will have to install certManager as well before enabling certManager properties in previous step.

helm repo add jetstack https://charts.jetstack.io
helm repo update
helm install cert-manager jetstack/cert-manager — namespace cert-manager — create-namespace — version v1.11.0 — set installCRDs=true
If everythings goes well, you will have three node nifi cluster up and running as below in 3/3 node cluster :