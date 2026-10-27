# Configuration Guide / コンフィグ設定ガイド

This document describes all configurable parameters for Standalone Chunk Destroyer.
本ドキュメントでは、Standalone Chunk Destroyer の全コンフィグ設定項目と設定可能な型・値の範囲を解説します。

**Config File Location / 設定ファイル保存場所**:
`.minecraft/config/chunkdestroyer-common.toml`

---

## 日本語 (Japanese)

| 設定項目キー | 型 | デフォルト値 | 設定可能範囲 | 説明 |
| :--- | :--- | :--- | :--- | :--- |
| `blocksPerTick` | 整数型 (`Integer`) | `64` | `1` ～ `2048` | 1tickあたりに採掘・破壊する最大ブロック数。アップグレード最大搭載相当の超高速動作を実現します。 |
| `mineBedrock` | 真偽値 (`Boolean`) | `false` | `true` / `false` | 岩盤（Bedrock）を採掘可能にするかどうか。`true` にすると最下層の岩盤も破壊・回収します。 |
| `removeFluids` | 真偽値 (`Boolean`) | `true` | `true` / `false` | 採掘範囲内の水や溶岩などの液体を自動消去するかどうか。水没や溶岩流によるラグを防ぎます。 |
| `maxRadiusBlocks` | 整数型 (`Integer`) | `512` | `16` ～ `4096` | チャンクデストロイヤー本体から設定可能な最大採掘半径（ブロック単位）。 |

### サンプルファイル (`chunkdestroyer-common.toml`)
```toml
[General]
	# 1tickあたりに採掘するブロック数（デフォルト: 64）
	# 範囲: 1 ~ 2048
	blocksPerTick = 64

	# 岩盤の採掘を許可するかどうか（デフォルト: false）
	mineBedrock = false

	# 液体（水・溶岩）を消去するかどうか（デフォルト: true）
	removeFluids = true

	# 最大採掘半径（ブロック単位、デフォルト: 512）
	# 範囲: 16 ~ 4096
	maxRadiusBlocks = 512
```

---

## English

| Config Key | Type | Default Value | Value Range | Description |
| :--- | :--- | :--- | :--- | :--- |
| `blocksPerTick` | `Integer` | `64` | `1` to `2048` | Maximum number of blocks mined per tick (Speed upgrade maximum equivalent). |
| `mineBedrock` | `Boolean` | `false` | `true` / `false` | Whether the Chunk Destroyer is allowed to mine bedrock. |
| `removeFluids` | `Boolean` | `true` | `true` / `false` | Whether to clear fluids (water, lava) in the mining zone to prevent fluid tick lag. |
| `maxRadiusBlocks` | `Integer` | `512` | `16` to `4096` | Maximum allowed mining radius in blocks from the machine block. |

### Example File (`chunkdestroyer-common.toml`)
```toml
[General]
	# Number of blocks mined per tick (Speed upgrade maximum default).
	# Range: 1 ~ 2048
	blocksPerTick = 64

	# Allow Chunk Destroyer to mine bedrock.
	# Range: true / false
	mineBedrock = false

	# Remove fluids (water, lava) in mining area without dropping entity lag.
	# Range: true / false
	removeFluids = true

	# Maximum mining radius in blocks from the machine.
	# Range: 16 ~ 4096
	maxRadiusBlocks = 512
```
