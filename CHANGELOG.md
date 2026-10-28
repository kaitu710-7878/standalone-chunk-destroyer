# Changelog / 変更履歴

All notable changes to this project will be documented in this file.
本プロジェクトにおける主要な変更履歴をこのファイルに記録します。

---

## [1.1.0-beta] - 2026-10-28

### 日本語 (Japanese)
- **ベータ版への移行 (Beta Release)**:
  - **パフォーマンス改善：無限仮置きストレージシステム**:
    - インベントリや隣接チェストが満杯の際、アイテムをワールド上にドロップ（エンティティ化）するのを完全に撤廃。
    - 超高速採掘時に大量のItemEntityが溢れて発生していた深刻なサーバーラグ・FPS低下をゼロにするため、内部の「仮置きストレージ」に安全に保管するよう改善。
    - ストレージやインベントリに空きスロットができ次第、毎tick仮置きストレージから優先的に自動搬出。
  - **安全な採掘開始制限**:
    - 仮置きストレージに未搬出のアイテムが残っている場合、新規に採掘を開始できない仕様を実装（UIの稼働開始ボタンが無効化・警告表示）。
    - ※すでに開始されている採掘の途中で仮置きにアイテムが溜まっても、採掘自体は中断されず続行されます。
  - **UI表示の追加**:
    - 画面上に仮置きアイテム数（仮置き: X個）をリアルタイム表示し、開始不可時の理由をわかりやすくツールチップで通知。

### English
- **Beta Phase Transition (Beta Release)**:
  - **Performance Optimization: Infinite Staging Buffer System**:
    - Completely removed world item drops (ItemEntities) when internal or adjacent inventories are full.
    - Mined items exceeding storage capacity are held in an internal staging buffer to eradicate physics and entity tick lag.
    - Automatically flushes items from the staging buffer into storages as soon as space is freed.
  - **Mining Start Restriction**:
    - Excavation cannot be newly started if items remain in the staging buffer (clicking ▶ Start is safely blocked with a warning).
    - *Note: Ongoing excavations will continue uninterrupted even if the staging buffer accumulates items while working.*
  - **UI Enhancements**:
    - Added real-time pending item count indicator (Pending: X) and warning tooltip explaining why start is locked.

---

## [1.0.3-alpha] - 2026-10-28
- 本家十字方位チャンク指定、垂直柱一括採掘、土・石削除フィルター機能。

## [1.0.2-alpha] - 2026-10-28
- 元MOD公式テクスチャ引用、GPL/LGPLデュアルライセンス化、日英ツールチップ対応。

## [1.0.1-alpha] - 2026-10-28
- 木のツルハシを含む作業台クラフトレシピ互換性修正、手動稼働開始仕様。

## [1.0.0-alpha] - 2026-10-28
- 初回Alphaリリース。