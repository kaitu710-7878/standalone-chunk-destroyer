# Standalone Chunk Destroyer (チャンクデストロイヤー・スタンドアロン)

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.3.0+-orange.svg)](https://files.minecraftforge.net/)
[![License: GPL-3.0](https://img.shields.io/badge/License-GPL--3.0-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

---

## 日本語 (Japanese)

### 概要
**Standalone Chunk Destroyer** は、dditional-enchanted-miner（QuarryPlus）の「チャンクデストロイヤー」をベースに、前提MOD（scalable-cats-force 等）への依存を完全に排除し、単体で動作するよう最適化・機能拡張された独立MODです。

### 主な特徴
1. **完全スタンドアロン動作 (No ScalableCatsForce / No Jar-in-Jar)**
   - ScalaランタイムやCats依存ライブラリ（scalable-cats-force）を一切要求しません。純粋なJavaとMinecraft Forgeのみで動作します。
2. **電力不要 (No Power Required)**
   - FE/RFなどの電力供給や外部エネルギーパイプが一切不要。設置するだけですぐに稼働します。
3. **デフォルトで最高速 (Maximum Speed Out of the Box)**
   - 高価なアップグレードカードの作成・装着を必要とせず、デフォルトで最高峰の採掘速度（1tickあたり64ブロック〜、コンフィグで調整可能）を誇ります。
4. **GUIでのキーボード数値直接入力 + ボタン操作**
   - 従来の+-ボタン（-16, -1, +1, +16）による範囲指定に加え、入力枠をクリックしてキーボードから任意の座標（Min/Max X, Y, Z）を直接数値入力可能です。
5. **隣接ストレージへの自動格納 (Adjacent Storage Auto-Push)**
   - 採掘したアイテムは、チャンクデストロイヤーに隣接するチェストや大容量ストレージ（IItemHandler）に自動的かつ高速に直接格納されます。
   - 隣接ストレージが存在しない、または満杯の場合は、27スロットの内部バッファに保持されます。
6. **圧倒的なパフォーマンス改善 (Optimized Performance)**
   - 大量のドロップアイテムをワールド内にItemEntityとしてスポーンさせず、直接アイテムスタックとして回収してインベントリへ搬送するため、物理演算ラグやクライアントFPS低下を極限まで抑制しています。
   - ブロック更新通知（Neighbor Updates）の連鎖を抑える最適化を行っており、巨大なチャンクを破壊してもサーバーTPSを維持します。
7. **作業台クラフトレシピ追加**
   - バニラの作業台で手軽に作成可能です。
   - **レシピ**: 中央に任意のピッケル（#forge:tools/pickaxes タグ指定）、周囲8スロットすべてにレッドストーンの粉。

### 導入方法
1. Minecraft 1.20.1 および Minecraft Forge（47.3.0以上推奨）をインストールします。
2. [Releases](../../releases) からビルド済みJARファイル（chunkdestroyer-1.0.0-alpha.jar）をダウンロードします。
3. ダウンロードしたJARファイルを .minecraft/mods フォルダに配置します。

---

## English

### Overview
**Standalone Chunk Destroyer** is a standalone, performance-optimized reimagining of the Chunk Destroyer from dditional-enchanted-miner (QuarryPlus). It completely eliminates external dependencies like scalable-cats-force and introduces extensive quality-of-life enhancements.

### Key Features
1. **Fully Standalone (No ScalableCatsForce / No Jar-in-Jar)**
   - Does not require Scala libraries or scalable-cats-force. Written in pure Java for Minecraft Forge 1.20.1.
2. **No Power Required (Free Energy)**
   - Operates without any RF/FE energy conduits or generators. Simply place the block down and it's ready to work.
3. **Maximum Speed by Default**
   - No need to craft or slot in expensive speed upgrades. It runs at peak mining speeds out of the box (64 blocks/tick by default, configurable).
4. **Direct Keyboard Coordinate Input & Button Controls in GUI**
   - In addition to standard +- buttons (-16, -1, +1, +16), you can click any coordinate field (Min/Max X, Y, Z) and directly type in numbers using your keyboard.
5. **Direct Adjacent Storage Auto-Insertion**
   - Automatically and swiftly routes mined items directly into adjacent storage containers (chests, drawers, barrels, and any Forge IItemHandler).
   - Retains items in an internal 27-slot buffer if no adjacent storage is available or if full.
6. **Dramatically Improved Performance**
   - Eliminates physics/entity lag by collecting drops directly into item stacks without spawning entities into the world.
   - Suppresses cascading neighbor block updates to preserve high server TPS even during massive chunk excavations.
7. **Crafting Table Recipe Added**
   - Easily crafted in a standard crafting table.
   - **Recipe**: Any pickaxe in the center (#forge:tools/pickaxes), surrounded by 8 Redstone Dust.

### Installation
1. Install Minecraft 1.20.1 and Minecraft Forge (47.3.0+).
2. Download the latest compiled JAR (chunkdestroyer-1.0.0-alpha.jar) from the [Releases](../../releases) tab.
3. Place the JAR file into your .minecraft/mods directory.