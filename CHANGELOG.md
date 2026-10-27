# Changelog / 変更履歴

All notable changes to this project will be documented in this file.
本プロジェクトにおける主要な変更履歴をこのファイルに記録します。

---

## [1.0.0-alpha] - 2026-10-28

### 日本語 (Japanese)
- **新規追加**:
  - scalable-cats-force の前提依存を完全排除した、Minecraft 1.20.1 Forge向けの独立型チャンクデストロイヤー（Chunk Destroyer）を新規実装。
  - 電力を消費せず稼働するエネルギーフリー仕様を実装。
  - デフォルトでアップグレード最大搭載相当の超高速採掘（デフォルト: 1tickあたり64ブロック採掘）を実現。
  - GUIにキーボードによる直接数値入力欄（EditBox）を実装。クリックしてMin/Max X, Y, Zをキーボード入力可能にし、同時に+-ボタン（-16, -1, +1, +16）でのクイック増減にも対応。
  - 周囲6面の隣接ストレージ（チェスト、バレル、外部Mod倉庫等）への自動・直接アイテム搬入システムを実装。
  - パフォーマンス改善: ItemEntityのワールド生成をバイパスし、ドロップを直接アイテムスタックとして回収・格納。ブロック更新通知（Neighbor Updates）の連鎖をブロックしサーバーTPS/クライアントFPSの低下を大幅改善。
  - 作業台クラフトレシピの追加（中央にタグ #forge:tools/pickaxes の任意のツルハシ、周囲8スロットにレッドストーンの粉）。
  - シルクタッチ / 幸運 III のGUI切り替え機能および液体（水・溶岩）一括消去トグル機能。
  - 日英バイリンガル対応（ja_jp.json, en_us.json, README, CHANGELOG, CONFIG）。

### English
- **Added**:
  - Standalone Chunk Destroyer for Minecraft 1.20.1 Forge without scalable-cats-force dependency.
  - Power-free operation (zero RF/FE energy needed).
  - Maximum speed out of the box (default: 64 blocks mined per tick).
  - Direct keyboard coordinate input via EditBoxes in GUI alongside +- adjustment buttons (-16, -1, +1, +16).
  - Direct adjacent storage item auto-insertion capability (supports all 6 sides, chests, drawers, barrels, etc.).
  - Drastic performance optimization: bypasses ItemEntity world spawning by accumulating direct loot drops, and prevents cascading neighbor updates for buttery-smooth server TPS.
  - Crafting table recipe (center: #forge:tools/pickaxes, surrounded by 8 Redstone Dust).
  - Silk Touch / Fortune III toggle and fluid removal mode toggle in GUI.
  - Full bilingual support (Japanese & English).