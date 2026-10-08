<div align="center">

  <h1>🅿️ Estacionamento Spring</h1>

  <p><strong>Cada entrada. Cada saída. Tudo sob controle.</strong></p>
  <p>Controle de vagas, visitas e histórico em uma aplicação web simples de operar.</p>

  <p>
    <img src="https://img.shields.io/badge/Java-25-E76F00?style=flat-square" alt="Java 25">
    <img src="https://img.shields.io/badge/Spring_Boot-4.1.0-6DB33F?style=flat-square&amp;logo=springboot&amp;logoColor=white" alt="Spring Boot 4.1.0">
    <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&amp;logo=thymeleaf&amp;logoColor=white" alt="Thymeleaf">
    <img src="https://img.shields.io/badge/H2-1E54B7?style=flat-square" alt="H2">
  </p>

  <p>
    <a href="https://estacionamento-spring-bn9p.onrender.com">
      <img src="https://img.shields.io/badge/Acessar_o_projeto-0066FF?style=for-the-badge&amp;logo=render&amp;logoColor=white" alt="Acessar a demonstração online no Render">
    </a>
  </p>
  <p><sub>Demonstração hospedada no Render. O primeiro acesso pode demorar enquanto o serviço inicia; os dados são temporários.</sub></p>

  <p>
    <a href="#sobre">Sobre</a> ·
    <a href="#funcionalidades">Funcionalidades</a> ·
    <a href="#interface">Interface</a> ·
    <a href="#tecnologias">Tecnologias</a>
  </p>

</div>

---

<a id="sobre"></a>

## 🚗 Sobre o projeto

O **Estacionamento Spring** organiza o dia a dia de um estacionamento: da chegada do veículo à consulta de suas visitas anteriores. O painel reúne entradas abertas, ocupação atual e vagas disponíveis, enquanto o histórico preserva os horários de entrada e saída.

A interface é responsiva e oferece dois perfis de acesso: o **Operador** registra entradas e saídas e consulta os dados; o **Admin** também gerencia usuários, capacidade e configurações. O foco do projeto é o controle das visitas, sem cobrança de tarifas.

<a id="funcionalidades"></a>

## ✨ Funcionalidades

| Recurso | O que oferece |
| --- | --- |
| **Painel** | Capacidade, ocupação, vagas disponíveis e entradas abertas. |
| **Entradas e saídas** | Registro dos veículos e encerramento das visitas com histórico preservado. |
| **Movimentações** | Consulta de visitas por placa e período de entrada. |
| **Relatórios** | Total de visitas, média de permanência e exportação em CSV. |
| **Usuários** | Contas com papéis de Admin e Operador e controle de acesso. |
| **Configurações** | Capacidade do pátio, notificações, exportação e backup SQL manual. |

<a id="interface"></a>

## 🖥️ Conheça a interface

![Painel do estacionamento com capacidade, ocupação, vagas disponíveis e entradas abertas](docs/images/painel.jpg)

<p align="center"><sub>Capturas da aplicação com dados fictícios de demonstração.</sub></p>

<details>
<summary><strong>Ver mais telas</strong></summary>

<br>

<table>
  <tr>
    <td width="50%" align="center">
      <strong>Login</strong><br><br>
      <img src="docs/images/login.jpg" alt="Login por email e senha" width="100%">
    </td>
    <td width="50%" align="center">
      <strong>Registro de entrada</strong><br><br>
      <img src="docs/images/entrada.jpg" alt="Registro de placa, modelo, cor e observação" width="100%">
    </td>
  </tr>
  <tr>
    <td width="50%" align="center">
      <strong>Relatórios</strong><br><br>
      <img src="docs/images/relatorios.jpg" alt="Relatórios com filtros, indicadores e exportação CSV" width="100%">
    </td>
    <td width="50%" align="center">
      <strong>Configurações</strong><br><br>
      <img src="docs/images/configuracoes.jpg" alt="Configurações de capacidade e recursos do sistema" width="100%">
    </td>
  </tr>
</table>

</details>

<details>
<summary><strong>Ver página de apresentação</strong></summary>

![Página de apresentação do Estacionamento Spring](docs/images/apresentacao.jpg)

</details>

<a id="tecnologias"></a>

## 🛠️ Tecnologias

Desenvolvido com **Java 25** e **Spring Boot 4.1.0**, usando **Spring MVC** e **Thymeleaf** para as páginas, **Spring Data JPA / Hibernate** para persistência e **H2** como banco de dados. A autenticação e as permissões são gerenciadas pelo **Spring Security**.

---

<div align="center">
  <sub>Estacionamento Spring · Da chegada ao histórico.</sub>
</div>
