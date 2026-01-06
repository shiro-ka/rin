# rin

## インストール

1. [Releases](https://github.com/USER_NAME/rin/releases) ページから最新の tar.gz をダウンロード
2. ファイルを解凍
3. `rin` と `rin.jar` を PATH の通ったディレクトリ（例: `/usr/local/bin` や `~/bin`）に移動
   ```bash
   # 例: ~/bin に入れる場合
   mkdir -p ~/bin
   mv rin rin.jar ~/bin/
   export PATH="$PATH:$HOME/bin"
   ```
   `rin` と `rin.jar` は同じディレクトリに置く必要があります

## 使用方法

`rin` コマンドを使用して、画像ファイルをランダムな名称に変更します。

### 基本

```bash
rin all
```
カレントディレクトリ直下の画像を12文字のランダムな名前に変更します。

### 引数の指定

- **パスの指定**:
  ```bash
  rin /path/to/directory  # ディレクトリ内の全ての画像
  rin my_image.png        # 特定のファイルのみ
  ```

- **文字数の指定**:
  ```bash
  rin 20                  # 20文字のランダム名
  ```

- **拡張子の指定**:
  ```bash
  rin txt doc             # .txt, .doc を対象にする
  ```
