🍔 README Miau&amp;Auau 🍔
Este README fornece instruções sobre como instalar e executar este projeto.&lt;br> E sim, o Swagger UI está rodando lindamente em! 😉

&lt;br>

🛠️ Pré-requisitos 🛠️
Antes de botar a mão na massa, você precisará ter as seguintes ferramentas instaladas na sua máquina:

☕ Java Development Kit (JDK): Versão 17.&lt;br> É crucial que a variável de ambiente JAVA_HOME esteja configurada direitinho e que o comando java -version retorne a versão 17.
💡 IntelliJ IDEA: Qualquer edição (Community ou Ultimate) vai dar conta do recado!
🐬 MySQL: Versão 8.0.&lt;br> Certifique-se de que o servidor MySQL esteja rodando a todo vapor e que você tenha as credenciais (usuário e senha) para acessar o banco de dados.
&lt;br>

⚙️ Instalação ⚙️
Siga estas etapas para deixar tudo tinindo:

&lt;br>

⬇️ Clone o repositório:

Bash

git clone https://github.com/boliveira01/backend-miaeauau
cd backend-miaeauau
&lt;br>

📂 Abra o projeto no IntelliJ IDEA:

Abra o seu IntelliJ IDEA.&lt;br>
Clique em "Open" ou "Open Project".&lt;br>
Navegue até a pasta onde você clonou o projeto e selecione-a.&lt;br>
O IntelliJ IDEA deve identificar o projeto como um projeto Java automaticamente.
&lt;br>

⚙️ Configure o banco de dados MySQL:

Verifique se o servidor MySQL está ligado.&lt;br>

Crie um novo banco de dados para o projeto, se ainda não existir. Use um cliente MySQL como o MySQL Workbench ou a linha de comando:

SQL

CREATE DATABASE miaueauau CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Crie um usuário MySQL com as permissões necessárias para acessar o banco de dados. Exemplo:

SQL

CREATE USER 'miau'@'localhost' IDENTIFIED BY 'auau';
GRANT ALL PRIVILEGES ON miaueauau.* TO 'miau'@'localhost';
FLUSH PRIVILEGES;
&lt;br>

✍️ Configure as informações de conexão com o banco de dados:

Procure o arquivo de configuração do banco de dados no seu projeto.&lt;br> Geralmente, ele se encontra em um arquivo como application.properties ou application.yml dentro da pasta de recursos (src/main/resources).

Edite este arquivo com os dados de conexão do seu banco de dados MySQL:

Properties

spring.datasource.url=jdbc:mysql://localhost:3306/miaueauau?useUnicode=true&characterEncoding=UTF-8&serverTimezone=America/Sao_Paulo
spring.datasource.username=miau
spring.datasource.password=auau
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
Lembre-se de substituir miaueauau, miau e auau pelas informações corretas.&lt;br> Ajuste o serverTimezone se precisar.

&lt;br>

▶️ Execute o projeto:

No IntelliJ IDEA, vá até a classe principal ClinicaVeterinariaApplication.&lt;br>
Clique com o botão direito nessa classe e selecione "Run".&lt;br>
O IntelliJ IDEA vai compilar e rodar o projeto.&lt;br> Fique de olho nos logs de inicialização no console! 👀
&lt;br>

➡️ Próximos Passos ➡️
🌐 Acesse o Swagger UI: Para interagir com a API e ver a documentação em tempo real, acesse o seguinte endereço no seu navegador: https://www.google.com/search?q=http://localhost:8080/swagger-ui/index.html%23/
📚 Dê uma olhada na documentação do projeto para entender todas as funcionalidades.
🧐 Monitore os logs da aplicação para verificar se há algum erro ou aviso.
Explore o código no IntelliJ IDEA para sacar a estrutura e a lógica do projeto.
&lt;br>

Obrigado!
