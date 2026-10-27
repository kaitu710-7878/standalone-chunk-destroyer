# Changelog / 変更履歴

All notable changes to this project will be documented in this file.
本プロジェクトにおける主要な変更履歴をこのファイルに記録します。

---

## [1.0.1-alpha] - 2026-10-28

### 日本語 (Japanese)
- **修正 & 改善**:
  - **クラフトレシピの互換性修正**: 単体Forge環境（他Mod未導入時）でタグ解決が行われないケースに対応するため、#forge:tools/pickaxes と #minecraft:pickaxes の両タグをMod内で定義・強化し、レシピの素材指定に木のツルハシをはじめとするバニラ全ピッケルを直接フォールバックとして追加。木のツルハシで確実に作業台クラフト可能に修正。
  - **設置時の自動稼働の廃止**: 設置直後は自動稼働せず、安全な停止待機状態（STOPPED）になるよう変更。UI内の「▶ 稼働開始」ボタンを押すことで任意のタイミングで稼働を開始・一時停止可能に修正。
  - UIの稼働ボタンのラベルを「▶ 稼働開始」/「⏸ 停止」にし、視認性を向上。

### English
- **Fixed & Improved**:
  - **Crafting Recipe Tag Compatibility**: Added internal tags for #forge:tools/pickaxes and #minecraft:pickaxes, and explicitly registered all vanilla pickaxes (including wooden pickaxe) directly in the crafting recipe ingredient list to guarantee crafting works in standalone environments.
  - **Prevent Automatic Mining on Placement**: The machine now defaults to a STOPPED state upon placement. Digging only starts when the player clicks the ▶ Start button in the UI.
  - Enhanced UI start/stop button label visibility with icons.

---

## [1.0.0-alpha] - 2026-10-28

### 日本語 (Japanese)
- 初回Alphaリリース。
- scalable-cats-force 依存排除、電力不要、デフォルト最高速度、キーボード直接数値入力GUI、隣接ストレージ自動格納、パフォーマンス改善。

### English
- Initial Alpha release.
- Standalone Forge 1.20.1 implementation without scalable-cats-force, power-free, maximum speed, keyboard input GUI, adjacent storage auto-push, and performance optimizations.