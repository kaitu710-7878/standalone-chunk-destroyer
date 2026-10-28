# Standalone Chunk Destroyer (チャンクデストロイヤー・スタンドアロン)

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.3.0+-orange.svg)](https://files.minecraftforge.net/)
[![License: GPL-3.0 or LGPL-3.0](https://img.shields.io/badge/License-GPL--3.0%20%7C%20LGPL--3.0-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)

---

## 日本語 (Japanese)

### 概要
**Standalone Chunk Destroyer** は、dditional-enchanted-miner（QuarryPlus / Kotori316氏作）の「チャンクデストロイヤー（AdvQuarry）」をベースに、前提MOD（scalable-cats-force 等）への依存を完全に排除し、単体で動作するよう最適化・機能拡張された独立MODです。
元MODの仕様に忠実な**「チャンク単位の十字方位範囲指定」**と**「垂直柱（Y全層）の一括破壊採掘」**、さらに**「土系・石系アイテムの自動消去（Void）機能」**を搭載しています。

### 主な特徴
1. **本家仕様：チャンク単位の十字方位範囲指定 (Authentic Chunk-Based Area)**
   - 設置チャンクを中心に、北(N)・南(S)・西(W)・東(E)への拡張チャンク数を指定する本家AdvQuarryの十字レイアウトを採用。
   - + / - ボタンでの増減（Shiftキーで16チャンク単位）に加え、**各入力欄をクリックしてキーボードから直接数値をタイピング入力可能**です。
2. **本家仕様：垂直柱（Y全層）の一括破壊採掘 (Vertical Column Destruction)**
   - 通常のクァーリーのように1層ずつ削るのではなく、X, Z座標の最上段から最下層（岩盤）までの垂直柱を一気に下まで掘り抜く、本物の「チャンクデストロイヤー」方式で採掘します。
   - デフォルトで最高速度（1tickに多数の垂直柱を一括掘削）で稼働します。
3. **土系 & 石系アイテムの自動消去（Void機能）**
   - UIのボタンからワンクリックで切り替え可能：
     - **石削除 (Stone: Void)**: 丸石、石、深層岩、安山岩、閃緑岩、花崗岩、凝灰岩、玄武岩、ブラックストーン等の石材を自動消去し、ストレージの圧迫を防止。
     - **土削除 (Dirt: Void)**: 土、草ブロック、砂利、砂、粗い土、泥等の土・砂系ブロックを自動消去。
4. **完全スタンドアロン動作 (No ScalableCatsForce / No Jar-in-Jar)**
   - ScalaランタイムやCats依存ライブラリ（scalable-cats-force）を一切要求しません。純粋なJavaとMinecraft Forgeのみで動作します。
5. **元MOD公式テクスチャの完全採用 (Original Textures Quoted)**
   - ブロックテクスチャ（上面・前面・側面）には、元MOD Additional-Enchanted-Miner（QuarryPlus）のAdvQuarry公式テクスチャを採用。
6. **安全設計：手動稼働開始 (Manual Start UI Button)**
   - 設置直後は待機状態（STOPPED）になります。UIを開いて範囲を設定後、「▶ 稼働開始」ボタンを押してスタートします。
7. **電力不要 (No Power Required)**
   - FE/RFなどの電力供給や外部エネルギーパイプが一切不要。
8. **隣接ストレージへの自動格納 (Adjacent Storage Auto-Push)**
   - 採掘したアイテムは、隣接するチェストや大容量ストレージ（IItemHandler）に自動的かつ高速に直接格納されます（内部バッファ付き）。
9. **完全バイリンガル対応 (日英ツールチップ & UI)**
   - アイテムツールチップ、UIタイトル、各ボタン（稼働、シルク/幸運、液体、石削除、土削除、適用、リセット、方位ボタン）のホバーツールチップに至るまで、日本語・英語の両言語に完全対応。
10. **確実な作業台クラフトレシピ (Crafting Table Recipe)**
   - 単独環境でも確実にクラフトできるよう、中央に木のツルハシをはじめとする任意のピッケル（タグ #forge:tools/pickaxes, #minecraft:pickaxes およびバニラ全ツルハシ直接対応）、周囲8スロットにレッドストーンの粉で作成可能です。

### ライセンス (License)
- 本MODは **GPL-3.0 または LGPL-3.0 (デュアルライセンス)** です。
- テクスチャおよびデザインは Kotori316 氏の Additional-Enchanted-Miner (LGPLv3) を引用しています。

### 導入方法
1. Minecraft 1.20.1 および Minecraft Forge（47.3.0以上推奨）をインストールします。
2. [Releases](../../releases) からビルド済みJARファイル（chunkdestroyer-1.0.3-alpha.jar）をダウンロードします。
3. ダウンロードしたJARファイルを .minecraft/mods フォルダに配置します。

---

## English

### Overview
**Standalone Chunk Destroyer** is a standalone, performance-optimized reimplementation of the Chunk Destroyer (AdvQuarry) from dditional-enchanted-miner (QuarryPlus by Kotori316). It features the authentic **chunk-based cross-directional range setting**, **full-depth vertical column excavation**, and **stone/dirt voiding filters**.

### Key Features
1. **Authentic Chunk-Based Cross-Directional Area**
   - Configures mining area by chunk expansion in four directions: North (N), South (S), West (W), and East (E) around the machine's chunk.
   - Use + / - buttons (hold Shift for 16 chunks) or **click any input field and directly type in numbers with your keyboard**.
2. **Authentic Vertical Column Excavation**
   - Mines all blocks from maxY down to minY (bedrock) in vertical columns at once.
   - Operates at peak maximum speed by default.
3. **Stone & Dirt Item Voiding Filters**
   - Toggleable directly in GUI:
     - **Stone Void**: Automatically discards cobblestone, stone, deepslate, granite, diorite, andesite, basalt, etc.
     - **Dirt Void**: Automatically discards dirt, grass, gravel, sand, mud, etc.
4. **Fully Standalone (No ScalableCatsForce / No Jar-in-Jar)**
   - Written in pure Java for Minecraft Forge 1.20.1. Zero Scala / Cats library dependencies.
5. **Authentic Original Textures Quoted**
   - Features the authentic AdvQuarry block textures from the original Additional-Enchanted-Miner (QuarryPlus).
6. **Safe Placement: Manual Start Button**
   - Defaults to STOPPED state upon placement. Open UI, review settings, and click ▶ Start.
7. **No Power Required (Free Energy)**
   - Operates without any RF/FE energy generators or cables.
8. **Direct Adjacent Storage Auto-Insertion**
   - Automatically routes mined items into adjacent storage containers (chests, barrels, drawers, etc.).
9. **Full Bilingual Support (Japanese & English Tooltips & UI)**
   - Every text element and interactive button hover tooltip is completely localized.
10. **Reliable Crafting Table Recipe**
   - Wooden pickaxes and all pickaxes (#forge:tools/pickaxes, #minecraft:pickaxes) in center, surrounded by 8 Redstone Dust.

### License
- Dual-licensed under **GPL-3.0 or LGPL-3.0**.
- Quoting textures and design references from Kotori316's Additional-Enchanted-Miner (LGPLv3).

### Installation
1. Install Minecraft 1.20.1 and Minecraft Forge (47.3.0+).
2. Download the latest compiled JAR (chunkdestroyer-1.0.3-alpha.jar) from the [Releases](../../releases) tab.
3. Place the JAR file into your .minecraft/mods directory.