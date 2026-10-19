#!/usr/bin/env python3
"""
Geocodifica abrigos usando o Nominatim.

Uso:
    python geocodificar_abrigos.py centro_acolhida_adultos.txt

Saída:
    centro_acolhida_adultos_geocodificado.txt
"""

import re
import sys
import time
from pathlib import Path

import requests

NOMINATIM_URL = "https://nominatim.openstreetmap.org/search"
USER_AGENT = "ProjetoAbrigoseSP/1.0 (contato@exemplo.com)"
DELAY_SEGUNDOS = 1.1

LINHA = re.compile(r'^\s*(\d+)\s+"(.*)"\s+"([^"]*)"\s*$')


def definir_arquivos():
    if len(sys.argv) < 2:
        raise SystemExit(
            "Uso: python geocodificar_abrigos.py <arquivo_entrada.txt>"
        )

    arquivo_entrada = Path(sys.argv[1])

    if not arquivo_entrada.exists():
        raise SystemExit(f"Arquivo não encontrado: {arquivo_entrada}")

    arquivo_saida = arquivo_entrada.with_name(
        arquivo_entrada.stem + "_geocodificado.txt"
    )

    return arquivo_entrada, arquivo_saida


def normalizar_endereco(endereco: str) -> str:
    endereco = endereco.strip()
    endereco = re.sub(r"\s+", " ", endereco)

    substituicoes = [
        (r"\s*-\s*(BRÁS|BOM RETIRO|BELA VISTA|CAMBUCI|PARI|LUZ|BARRA FUNDA|"
         r"SANTA CECÍLIA|LIBERDADE|REPÚBLICA|VILA LEOPOLDINA|VILA ALPINA|"
         r"VILA MARIETA|FAZENDA DO CARMO|PLANALTO PAULISTA|CAMPOS ELISEOS|"
         r"VILA CONSTÂNCIA)\s*$", ""),
        (r"\s*\(.*?\)\s*$", ""),
        (r"\s*,?\s*HOTEL\s+.*$", ""),
        (r"\s*-\s*HOTEL\s+.*$", ""),
        (r"\s+S/N[ºO°]?\.?\s*$", ""),
        (r"\s+ESQUINA COM .*$", ""),
    ]

    for padrao, substituto in substituicoes:
        endereco = re.sub(padrao, substituto, endereco, flags=re.IGNORECASE)

    return endereco.strip()


def geocodificar(endereco: str):
    consulta = f"{endereco}, São Paulo, SP, Brasil"

    params = {
        "q": consulta,
        "format": "json",
        "limit": 1,
        "addressdetails": 0,
        "countrycodes": "br",
    }

    headers = {
        "User-Agent": USER_AGENT,
        "Accept-Language": "pt-BR,pt;q=0.9",
    }

    resposta = requests.get(
        NOMINATIM_URL,
        params=params,
        headers=headers,
        timeout=20,
    )

    if resposta.status_code == 429:
        print("Limite de requisições atingido. Aguardando 60 segundos...")
        time.sleep(60)
        return None

    resposta.raise_for_status()
    dados = resposta.json()

    if not dados:
        return None

    resultado = dados[0]
    return f"{resultado['lat']} {resultado['lon']}"


def main():
    arquivo_entrada, arquivo_saida = definir_arquivos()

    linhas_saida = []
    total = 0
    preenchidos = 0
    falhas = []

    for linha_original in arquivo_entrada.read_text(encoding="utf-8").splitlines():
        if not linha_original.strip():
            continue

        correspondencia = LINHA.match(linha_original)

        if not correspondencia:
            print(f"Linha ignorada por formato inválido: {linha_original}")
            continue

        indice, descricao, coordenadas = correspondencia.groups()
        total += 1

        if coordenadas.strip():
            linhas_saida.append(
                f'{indice} "{descricao}" "{coordenadas.strip()}"'
            )
            preenchidos += 1
            continue

        if " - " not in descricao:
            falhas.append(
                (indice, descricao, "Descrição sem endereço separado por ' - '")
            )
            linhas_saida.append(f'{indice} "{descricao}" ""')
            continue

        endereco = descricao.split(" - ", 1)[1]
        endereco = normalizar_endereco(endereco)

        print(f"Geocodificando [{indice}]: {endereco}")

        try:
            coordenada = geocodificar(endereco)
        except requests.RequestException as erro:
            coordenada = None
            falhas.append((indice, descricao, f"Erro de requisição: {erro}"))

        if coordenada:
            preenchidos += 1
            linhas_saida.append(f'{indice} "{descricao}" "{coordenada}"')
            print(f"  Encontrado: {coordenada}")
        else:
            falhas.append((indice, descricao, "Endereço não encontrado"))
            linhas_saida.append(f'{indice} "{descricao}" ""')
            print("  Não encontrado.")

        time.sleep(DELAY_SEGUNDOS)

    arquivo_saida.write_text(
        "\n".join(linhas_saida) + "\n",
        encoding="utf-8",
    )

    print()
    print(f"Arquivo gerado: {arquivo_saida}")
    print(f"Total de registros: {total}")
    print(f"Registros com coordenada: {preenchidos}")
    print(f"Registros sem coordenada: {total - preenchidos}")

    if falhas:
        print("\nRegistros que precisam de revisão manual:")
        for indice, descricao, motivo in falhas:
            print(f"- [{indice}] {descricao} — {motivo}")


if __name__ == "__main__":
    main()