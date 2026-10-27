# Standalone Chunk Destroyer (チャンクデストロイヤー・スタンドアロン)

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.3.0+-orange.svg)](https://files.minecraftforge.net/)
[![License: GPL-3.0 or LGPL-3.0](https://img.shields.io/badge/License-GPL--3.0%20%7C%20LGPL--3.0-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)

---

## 日本語 (Japanese)

### 概要
**Standalone Chunk Destroyer** は、dditional-enchanted-miner（QuarryPlus / Kotori316氏作）の「チャンクデストロイヤー」をベースに、前提MOD（scalable-cats-force 等）への依存を完全に排除し、単体で動作するよう最適化・機能拡張された独立MODです。
元MODのライセンス（LGPLv3）を継承し、**GPL-3.0 および LGPL-3.0 のデュアルライセンス**で提供されています。

### 主な特徴
1. **完全スタンドアロン動作 (No ScalableCatsForce / No Jar-in-Jar)**
   - ScalaランタイムやCats依存ライブラリ（scalable-cats-force）を一切要求しません。純粋なJavaとMinecraft Forgeのみで軽快に動作します。
2. **元MOD公式テクスチャの完全継承 (Original Textures Quoted)**
   - ブロックテクスチャ（上面・前面・側面）には、元MOD Additional-Enchanted-Miner（QuarryPlus）のAdvQuarry公式テクスチャをそのまま引用・採用しており、親しみやすいデザインとなっています。
3. **安全設計：手動稼働開始 (Manual Start UI Button)**
   - 設置した瞬間に勝手に掘り始める事故を防ぐため、設置直後は待機状態（STOPPED）になります。UIを開いて範囲を確認・調整後、「▶ 稼働開始」ボタンを押してスタートします。
4. **電力不要 (No Power Required)**
   - FE/RFなどの電力供給や外部エネルギーパイプが一切不要。設置してUIから稼働開始するだけですぐに採掘できます。
5. **デフォルトで最高速 (Maximum Speed Out of the Box)**
   - アップグレードカード不要で、デフォルトで最高峰の採掘速度（1tickあたり64ブロック〜、コンフィグで調整可能）を誇ります。
6. **GUIでのキーボード数値直接入力 + ボタン操作**
   - 従来の+-ボタン（-16, -1, +1, +16）に加え、入力枠をクリックしてキーボードから任意の座標（Min/Max X, Y, Z）を直接数値入力可能です。
7. **隣接ストレージへの自動格納 (Adjacent Storage Auto-Push)**
   - 採掘したアイテムは、隣接するチェストや大容量ストレージ（IItemHandler）に自動的かつ高速に直接格納されます（内部27スロットバッファ付き）。
8. **完全バイリンガル対応 (日英ツールチップ & UI)**
   - アイテムツールチップ、UIタイトル、各ボタン（稼働切替、シルク/幸運切替、液体消去切替、座標適用、リセット、数値調整ボタン）のホバーツールチップに至るまで、日本語・英語の両言語に完全対応しています。
9. **圧倒的なパフォーマンス改善 (Optimized Performance)**
   - ドロップアイテムのワールドスポーンをバイパスし、ブロック更新通知連鎖を抑える最適化によりサーバーTPSを維持します。
10. **確実な作業台クラフトレシピ (Crafting Table Recipe)**
   - 単独環境でも確実にクラフトできるよう、中央に木のツルハシをはじめとする任意のピッケル（タグ #forge:tools/pickaxes, #minecraft:pickaxes およびバニラ全ツルハシ直接対応）、周囲8スロットにレッドストーンの粉で作成可能です。

### ライセンス (License)
- 本MODは **GPL-3.0 または LGPL-3.0 (デュアルライセンス)** です。
- テクスチャおよびデザインは Kotori316 氏の Additional-Enchanted-Miner (LGPLv3) を引用しています。

### 導入方法
1. Minecraft 1.20.1 および Minecraft Forge（47.3.0以上推奨）をインストールします。
2. [Releases](../../releases) からビルド済みJARファイル（chunkdestroyer-1.0.2-alpha.jar）をダウンロードします。
3. ダウンロードしたJARファイルを .minecraft/mods フォルダに配置します。

---

## English

### Overview
**Standalone Chunk Destroyer** is a standalone, performance-optimized reimagining of the Chunk Destroyer from dditional-enchanted-miner (QuarryPlus by Kotori316). It completely eliminates external dependencies like scalable-cats-force and introduces extensive quality-of-life enhancements.
Licensed under both **GPL-3.0 and LGPL-3.0 (Dual License)** adhering to the original mod's LGPLv3 terms.

### Key Features
1. **Fully Standalone (No ScalableCatsForce / No Jar-in-Jar)**
   - Does not require Scala libraries or scalable-cats-force. Written in pure Java for Minecraft Forge 1.20.1.
2. **Original Textures Quoted**
   - Features the authentic AdvQuarry block textures directly quoted from the original Additional-Enchanted-Miner (QuarryPlus) under LGPLv3.
3. **Safe Placement: Manual Start Button**
   - Does not start digging automatically upon placement. Placed in a safe STOPPED state; open the UI, configure your bounds, and press the ▶ Start button.
4. **No Power Required (Free Energy)**
   - Operates without any RF/FE energy conduits or generators.
5. **Maximum Speed by Default**
   - No need to slot in speed upgrades. Runs at peak mining speeds out of the box (64 blocks/tick by default, configurable).
6. **Direct Keyboard Coordinate Input & Button Controls in GUI**
   - Click any coordinate field (Min/Max X, Y, Z) and directly type in numbers, or use quick +- adjustment buttons (-16, -1, +1, +16).
7. **Direct Adjacent Storage Auto-Insertion**
   - Automatically routes mined items into adjacent storage containers (chests, drawers, barrels, and Forge IItemHandler).
8. **Full Bilingual Support (Japanese & English Tooltips & UI)**
   - Every text element including item tooltips, screen titles, and interactive button hover tooltips (Start/Stop, Silk/Fortune, Fluids, Apply, Reset, Step adjustments) is fully translated in English and Japanese.
9. **Dramatically Improved Performance**
   - Direct item stack collection without entity lag, and suppresses neighbor update chains.
10. **Reliable Crafting Table Recipe**
   - Compatible even in standalone setups: wooden pickaxes and all pickaxes (#forge:tools/pickaxes, #minecraft:pickaxes, vanilla pickaxes directly matched) in the center, surrounded by 8 Redstone Dust.

### License
- Dual-licensed under **GPL-3.0 or LGPL-3.0**.
- Quoting textures and design references from Kotori316's Additional-Enchanted-Miner (licensed under LGPLv3).

### Installation
1. Install Minecraft 1.20.1 and Minecraft Forge (47.3.0+).
2. Download the latest compiled JAR (chunkdestroyer-1.0.2-alpha.jar) from the [Releases](../../releases) tab.
3. Place the JAR file into your .minecraft/mods directory.