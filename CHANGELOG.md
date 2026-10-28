# Changelog / 変更履歴

All notable changes to this project will be documented in this file.
本プロジェクトにおける主要な変更履歴をこのファイルに記録します。

---

## [1.0.3-alpha] - 2026-10-28

### 日本語 (Japanese)
- **本家チャンクデストロイヤー仕様への完全移行**:
  - **採掘範囲の指定方式**: 設置チャンクを中心に、北(N)・南(S)・西(W)・東(E)への拡張チャンク数を指定する本家AdvQuarryスタイルの十字レイアウトを採用。+ / - ボタン（Shiftで16チャンク単位）に加え、各入力欄（EditBox）をクリックしてキーボードから数値を直接タイピング入力可能。
  - **採掘方式の刷新**: 従来のY層スキャンから、X, Z座標の最上段から最下層（岩盤）まで垂直柱を一気に下まで掘り抜く、本物のチャンクデストロイヤー垂直柱掘削アルゴリズムに刷新。
  - **土系・石系アイテムの削除機能 (Void Filters)**:
    - UIボタンによる「石削除 (Stone: Void)」および「土削除 (Dirt: Void)」トグル機能を追加。
    - 丸石、石、深層岩などの石材や、土、砂利、砂などの不要な建材を採掘時に自動消去し、ストレージの圧迫を防止。
- **UIテクスチャおよびレイアウトの再設計**: 十字方位チャンク指定と新ボタン群に合わせてGUIを完全最適化。

### English
- **Full Alignment with Authentic Chunk Destroyer Mechanics**:
  - **Chunk-Based Range Setting**: Adopted the authentic AdvQuarry cross-directional layout configuring chunk expansion for North (N), South (S), West (W), and East (E) around the center chunk. Supports + / - buttons (Shift for 16 chunks) and direct keyboard typing into EditBoxes.
  - **Vertical Column Excavation**: Switched excavation logic to authentic Chunk Destroyer mechanics: excavating full vertical columns from maxY down to bedrock at once.
  - **Stone & Dirt Item Voiding Filters**:
    - Added UI toggle buttons for "Stone: Void" and "Dirt: Void".
    - Automatically discards cobblestone, stone, deepslate, dirt, gravel, sand, etc., preventing inventory overflow.
- **Redesigned GUI Layout & Texture**: Updated GUI to fit cross-directional chunk controls and new feature toggles.

---

## [1.0.2-alpha] - 2026-10-28
- 元MOD公式テクスチャの引用、GPL/LGPLデュアルライセンス化、日英ツールチップ対応。

## [1.0.1-alpha] - 2026-10-28
- 木のツルハシを含む作業台クラフトレシピの互換性修正、手動稼働開始仕様。

## [1.0.0-alpha] - 2026-10-28
- 初回Alphaリリース。