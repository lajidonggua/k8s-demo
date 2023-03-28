# k8s-demo
k8s demo 单机



Main modify

# 环境：
腾讯云轻量服务器， centos
cat /etc/redhat-release 查看版本
# 安装docker
## 设置仓库： sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
## 设置加速源
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
"registry-mirrors": ["https://nmxk8hna.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
## 安装
yum install -y docker-ce-19.03.14 docker-ce-cli-19.03.14
## 启动
sudo systemctl start docker && sudo systemctl enable docker
## 检查是否成功启动
ps -ef|grep docker
# 设置hostName
```shell
## master节点用master node节点用node1或者自定义
sudo hostnamectl set-hostname k8s-master 
bash ## 刷新一下看看名称有没有改成功

# 修改映射
cat >> /etc/hosts << EOF
110.40.158.88 k8s-master
```

# 系统配置

```shell
# (1)关闭防火墙
systemctl stop firewalld && systemctl disable firewalld

# (2)关闭selinux
setenforce 0
sed -i 's/^SELINUX=enforcing$/SELINUX=permissive/' /etc/selinux/config

# (3)关闭swap
swapoff -a
sed -i '/swap/s/^\(.*\)$/#\1/g' /etc/fstab

# (4)配置iptables的ACCEPT规则
iptables -F && iptables -X && iptables -F -t nat && iptables -X -t nat && iptables -P FORWARD ACCEPT

# (5)设置系统参数
cat <<EOF >  /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF

sysctl --system

```

# 安装 kubectl，kubelet ，kubeadm

```shell
cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=http://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=http://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg
       http://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF

yum install -y kubectl-1.23.1-0
yum install -y kubelet-1.23.1-0
yum install -y kubeadm-1.23.1-0
# 查看安装后的版本。
kubectl version
kubelet --version
kubeadm version

systemctl enable kubelet

# 编辑docker的daemon.json文件,每个节点都要执行
vi /etc/docker/daemon.json
# 增加这行
"exec-opts": ["native.cgroupdriver=systemd"]
    
systemctl restart docker

# 看一下你的公网IP是否在机器中存在
ip a | grep 110.40.158.88

# 如果没有就执行下面这段代码新增一个和公网IP一样的虚拟网卡IP
cat > /etc/sysconfig/network-scripts/ifcfg-eth0:1 <<EOF
BOOTPROTO=static
DEVICE=eth0:1
IPADDR=110.40.158.88
PREFIX=32
TYPE=Ethernet
USERCTL=no
ONBOOT=yes
EOF


systemctl restart network

# 初始化
  kubeadm init --image-repository registry.aliyuncs.com/google_containers \
  --kubernetes-version=1.23.1 \
  --pod-network-cidr=192.168.0.0/16 \
  --control-plane-endpoint=110.40.158.88 \
  --apiserver-advertise-address=110.40.158.88
  
mkdir -p $HOME/.kube
cd .kube/
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
vim /etc/profile
# 添加以下内容永久生效
export KUBECONFIG="/etc/kubernetes/admin.conf"
# 执行一下
source /etc/profile
```



