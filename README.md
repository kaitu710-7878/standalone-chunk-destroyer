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
   - FE/RFなどの電力供給や外部エネルギーパイプが一切不要。設置してUIから稼働開始するだけですぐに採掘できます。
3. **安全設計：手動稼働開始 (Manual Start UI Button)**
   - 設置した瞬間に勝手に掘り始める事故を防ぐため、設置直後は待機状態（STOPPED）になります。UIを開いて範囲を確認・調整後、「▶ 稼働開始」ボタンを押してスタートします。
4. **デフォルトで最高速 (Maximum Speed Out of the Box)**
   - アップグレードカード不要で、デフォルトで最高峰の採掘速度（1tickあたり64ブロック〜、コンフィグで調整可能）を誇ります。
5. **GUIでのキーボード数値直接入力 + ボタン操作**
   - 従来の+-ボタン（-16, -1, +1, +16）に加え、入力枠をクリックしてキーボードから任意の座標（Min/Max X, Y, Z）を直接数値入力可能です。
6. **隣接ストレージへの自動格納 (Adjacent Storage Auto-Push)**
   - 採掘したアイテムは、隣接するチェストや大容量ストレージ（IItemHandler）に自動的かつ高速に直接格納されます（内部27スロットバッファ付き）。
7. **圧倒的なパフォーマンス改善 (Optimized Performance)**
   - ドロップアイテムのワールドスポーンをバイパスし、ブロック更新通知連鎖を抑える最適化によりサーバーTPSを維持します。
8. **確実な作業台クラフトレシピ (Crafting Table Recipe)**
   - 他MODなしの単独環境でも確実にクラフトできるよう、中央に木のツルハシをはじめとする任意のピッケル（タグ #forge:tools/pickaxes, #minecraft:pickaxes およびバニラ全ツルハシ直接対応）、周囲8スロットにレッドストーンの粉で作成可能です。

### 導入方法
1. Minecraft 1.20.1 および Minecraft Forge（47.3.0以上推奨）をインストールします。
2. [Releases](../../releases) からビルド済みJARファイル（chunkdestroyer-1.0.1-alpha.jar）をダウンロードします。
3. ダウンロードしたJARファイルを .minecraft/mods フォルダに配置します。

---

## English

### Overview
**Standalone Chunk Destroyer** is a standalone, performance-optimized reimagining of the Chunk Destroyer from dditional-enchanted-miner (QuarryPlus). It completely eliminates external dependencies like scalable-cats-force and introduces extensive quality-of-life enhancements.

### Key Features
1. **Fully Standalone (No ScalableCatsForce / No Jar-in-Jar)**
   - Does not require Scala libraries or scalable-cats-force. Written in pure Java for Minecraft Forge 1.20.1.
2. **No Power Required (Free Energy)**
   - Operates without any RF/FE energy conduits or generators.
3. **Safe Placement: Manual Start Button**
   - Does not start digging automatically upon placement. Placed in a safe STOPPED state; open the UI, configure your bounds, and press the ▶ Start button.
4. **Maximum Speed by Default**
   - No need to slot in speed upgrades. Runs at peak mining speeds out of the box (64 blocks/tick by default, configurable).
5. **Direct Keyboard Coordinate Input & Button Controls in GUI**
   - Click any coordinate field (Min/Max X, Y, Z) and directly type in numbers, or use quick +- adjustment buttons (-16, -1, +1, +16).
6. **Direct Adjacent Storage Auto-Insertion**
   - Automatically routes mined items into adjacent storage containers (chests, drawers, barrels, and Forge IItemHandler).
7. **Dramatically Improved Performance**
   - Direct item stack collection without entity lag, and suppresses neighbor update chains.
8. **Reliable Crafting Table Recipe**
   - Compatible even in standalone setups: wooden pickaxes and all pickaxes (#forge:tools/pickaxes, #minecraft:pickaxes, vanilla pickaxes directly matched) in the center, surrounded by 8 Redstone Dust.

### Installation
1. Install Minecraft 1.20.1 and Minecraft Forge (47.3.0+).
2. Download the latest compiled JAR (chunkdestroyer-1.0.1-alpha.jar) from the [Releases](../../releases) tab.
3. Place the JAR file into your .minecraft/mods directory.