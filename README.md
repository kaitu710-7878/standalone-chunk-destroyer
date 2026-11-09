# Standalone Chunk Destroyer (チャンクデストロイヤー・スタンドアロン)

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.3.0+-orange.svg)](https://files.minecraftforge.net/)
[![Release: Beta](https://img.shields.io/badge/Release-Beta%20(v1.1.2)--beta-blueviolet.svg)](https://github.com/kaitu710-7878/standalone-chunk-destroyer/releases)
[![License: GPL-3.0 or LGPL-3.0](https://img.shields.io/badge/License-GPL--3.0%20%7C%20LGPL--3.0-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)

---

## 日本語 (Japanese)

### 概要
**Standalone Chunk Destroyer** は、dditional-enchanted-miner（QuarryPlus / Kotori316氏作）の「チャンクデストロイヤー（AdvQuarry）」をベースに、前提MOD（scalable-cats-force 等）への依存を完全に排除し、単体で動作するよう最適化・機能拡張された独立MODです。
最新の **Beta版（v1.1.2-beta）** では、アイテムのドロップ化によるサーバーラグを完全根絶する**「無限仮置きストレージシステム」**と**「安全な採掘開始制限」**に加え、**256pxワイドUI（中央十字チャンク指定・右側ボタン群・左側ステータス表示）**を搭載しています。

### 主な特徴
1. **256pxワイドGUI＆最適化レイアウト (Wide GUI & Ergonomic Layout)**
   - UIの密度を解消するため横幅256pxに拡張。
   - **左側**: 稼働状態（稼働中/停止中）、進捗率、仮置きアイテム数をリアルタイム表示。
   - **中央**: 十字方位チャンク指定（北・南・西・東）とキーボード直接数値入力枠・増減ボタン。Sラベルのめり込みも完全解消。
   - **右側**: 縦並びの操作ボタン群（開始/停止、シルク/幸運、液体、石削除、土削除、リセット）。
2. **パフォーマンス改善：無限仮置きストレージシステム (Infinite Staging Buffer)**
   - 内部インベントリや隣接ストレージが満杯になった場合、アイテムをワールド上にドロップ（エンティティ化）せず、チャンクデストロイヤー内部の仮置きストレージに安全に保管します。
   - 大量のドロップアイテム（ItemEntity）がワールドに溢れることによる激しいサーバーTPS低下やクライアントFPS低下を完全に防止します。
   - ストレージやインベントリに空きができ次第、毎tick仮置きストレージから優先的に自動補充・搬出されます。
2. **安全な採掘開始制限 (Staging Buffer Start Gate)**
   - 仮置きストレージに未搬出のアイテムが残っている場合、新規に採掘を開始することはできません（UIから「▶ 稼働開始」を押しても安全にブロックされます）。
   - ※すでに開始されている採掘の途中で仮置きにアイテムが溜まっても、採掘が中断されることはありません（開始時のみ判定）。
3. **本家仕様：チャンク単位の十字方位範囲指定 (Authentic Chunk-Based Area)**
   - 設置チャンクを中心に、北(N)・南(S)・西(W)・東(E)への拡張チャンク数を指定する本家AdvQuarryの十字レイアウトを採用。
   - + / - ボタンでの増減（Shiftキーで16チャンク単位）に加え、**各入力欄をクリックしてキーボードから直接数値をタイピング入力可能**です。
4. **本家仕様：垂直柱（Y全層）の一括破壊採掘 (Vertical Column Destruction)**
   - X, Z座標の最上段から最下層（岩盤）までの垂直柱を一気に下まで掘り抜く、本物の「チャンクデストロイヤー」方式で採掘します。
   - デフォルトで最高速度（1tickに多数の垂直柱を一括掘削）で稼働します。
5. **土系 & 石系アイテムの自動消去（Void機能）**
   - UIのボタンからワンクリックで切り替え可能：
     - **石削除 (Stone: Void)**: 丸石、石、深層岩、安山岩、閃緑岩、花崗岩等の石材を自動消去。
     - **土削除 (Dirt: Void)**: 土、草ブロック、砂利、砂等の土・砂系ブロックを自動消去。
6. **完全スタンドアロン動作 (No ScalableCatsForce / No Jar-in-Jar)**
   - ScalaランタイムやCats依存ライブラリ（scalable-cats-force）を一切要求しません。純粋なJavaとMinecraft Forgeのみで動作します。
7. **元MOD公式テクスチャの完全採用 (Original Textures Quoted)**
   - ブロックテクスチャ（上面・前面・側面）には、元MOD Additional-Enchanted-Miner（QuarryPlus）のAdvQuarry公式テクスチャを採用。
8. **安全設計：手動稼働開始 (Manual Start UI Button)**
   - 設置直後は待機状態（STOPPED）になります。UIを開いて範囲を設定後、「▶ 稼働開始」ボタンを押してスタートします。
9. **電力不要 (No Power Required)**
   - FE/RFなどの電力供給や外部エネルギーパイプが一切不要。
10. **隣接ストレージへの自動格納 (Adjacent Storage Auto-Push)**
    - 採掘したアイテムは、隣接するチェストや大容量ストレージ（IItemHandler）に自動的かつ高速に直接格納されます。
11. **確実な作業台クラフトレシピ (Crafting Table Recipe)**
    - 中央に木のツルハシをはじめとする任意のピッケル（タグ #forge:tools/pickaxes, #minecraft:pickaxes およびバニラ全ツルハシ直接対応）、周囲8スロットにレッドストーンの粉で作成可能です。

### ライセンス (License)
- 本MODは **GPL-3.0 または LGPL-3.0 (デュアルライセンス)** です。
- テクスチャおよびデザインは Kotori316 氏の Additional-Enchanted-Miner (LGPLv3) を引用しています。

### 導入方法
1. Minecraft 1.20.1 および Minecraft Forge（47.3.0以上推奨）をインストールします。
2. [Releases](../../releases) からビルド済みJARファイル（chunkdestroyer-1.1.2-beta.jar）をダウンロードします。
3. ダウンロードしたJARファイルを .minecraft/mods フォルダに配置します。

---

## English

### Overview
**Standalone Chunk Destroyer** is a standalone, performance-optimized reimplementation of the Chunk Destroyer (AdvQuarry) from dditional-enchanted-miner (QuarryPlus by Kotori316).
In this **Beta release (v1.1.2-beta)**, it introduces a **256px Wide GUI Layout** (Left: Status/Buffer, Center: Cross Chunk Controls with no overlaps, Right: Action Buttons), alongside the **Infinite Staging Buffer system** and **Safe Mining Start Gate**.

### Key Features
1. **256px Wide Screen & Ergonomic UI Layout**
   - High-density UI completely resolved by expanding the GUI texture to 256px.
   - **Left**: Real-time status indicator, mining progress, and staged buffer item count.
   - **Center**: Cross-directional chunk bounds (N, S, W, E) with keyboard numeric input & +/- buttons. Fixed "S" label overlap.
   - **Right**: Vertically aligned action buttons (Start/Stop, Silk/Fortune, Fluids, Void Stone, Void Dirt, Reset).
2. **Performance Optimization: Infinite Staging Buffer**
   - Mined items are never dropped into the world as entities when internal/adjacent storages are full; they are safely stored in an internal infinite staging buffer.
   - Completely prevents severe server TPS drops and client FPS degradation caused by thousands of ItemEntities.
   - Automatically flushes items into adjacent storages as soon as free slots become available.
2. **Safe Mining Start Gate**
   - New mining operations cannot be started if items remain in the staging buffer (clicking ▶ Start in UI is safely prevented).
   - *Note: This gate only affects starting new excavations; an ongoing excavation will never be halted if items accumulate in the staging buffer while working.*
3. **Authentic Chunk-Based Cross-Directional Area**
   - Configures mining area by chunk expansion in four directions: North (N), South (S), West (W), and East (E) around the center chunk.
   - Supports + / - buttons (Shift for 16 chunks) and **direct keyboard typing into EditBoxes**.
4. **Authentic Vertical Column Excavation**
   - Mines all blocks from maxY down to minY (bedrock) in full vertical columns at once at peak maximum speed.
5. **Stone & Dirt Item Voiding Filters**
   - Direct GUI toggles for "Stone: Void" and "Dirt: Void" to automatically discard cobblestone, stone, deepslate, dirt, gravel, sand, etc.
6. **Fully Standalone (No ScalableCatsForce / No Jar-in-Jar)**
   - Pure Java 17 for Minecraft Forge 1.20.1. Zero Scala / Cats library dependencies.
7. **Authentic Original Textures Quoted**
   - Features the authentic AdvQuarry block textures from the original Additional-Enchanted-Miner (QuarryPlus).
8. **No Power Required (Free Energy)**
   - Operates without any RF/FE energy generators or cables.
9. **Direct Adjacent Storage Auto-Insertion**
   - Automatically routes mined items into adjacent storage containers (chests, barrels, drawers, etc.).
10. **Reliable Crafting Table Recipe**
    - Wooden pickaxes and all pickaxes (#forge:tools/pickaxes, #minecraft:pickaxes) in center, surrounded by 8 Redstone Dust.

### License
- Dual-licensed under **GPL-3.0 or LGPL-3.0**.
- Quoting textures and design references from Kotori316's Additional-Enchanted-Miner (LGPLv3).

### Installation
1. Install Minecraft 1.20.1 and Minecraft Forge (47.3.0+).
2. Download the latest compiled JAR (chunkdestroyer-1.1.2-beta.jar) from the [Releases](../../releases) tab.
3. Place the JAR file into your .minecraft/mods directory.