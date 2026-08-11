#!/usr/bin/env python3
"""
Remove from the shipped tree every file the asset sidecar restores at runtime.

This is the other half of the sidecar, and it is driven by the same manifest so the two
cannot drift: every path on the right-hand side of `asset-sidecar.properties` is a file
this repository must NOT contain, because the sidecar's whole purpose is to read that
file from a copy the player supplies instead.

    python tools/strip_sidecar_assets.py --check     # fail if any sidecar-supplied file is present
    python tools/strip_sidecar_assets.py --dry-run   # list what would go
    python tools/strip_sidecar_assets.py             # delete them

`--check` is the one that matters: it runs in the pre-publish audit and in CI, and it is
what stops a stray `git checkout` or a re-run of the old importer from quietly putting
the original mod's art back into a public repository. It exits non-zero if anything is found.

Deleting is idempotent and safe to re-run. Empty directories left behind are removed too,
so the tree does not fill up with husks.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parents[1]
MANIFEST = REPO / "src/main/resources/assets/elementalcreepers/asset-sidecar.properties"
RESOURCES = REPO / "src/main/resources"


def sidecar_paths() -> list[str]:
    """Every pack path the manifest promises to restore, relative to src/main/resources."""
    if not MANIFEST.is_file():
        sys.exit(f"manifest not found: {MANIFEST}")

    # Join the backslash continuations the manifest uses for multi-value entries before
    # splitting into lines, so a two-path value is not read as one path and one orphan.
    text = re.sub(r"\\\s*\n\s*", "", MANIFEST.read_text(encoding="utf-8"))

    paths: list[str] = []
    for line in text.splitlines():
        line = line.strip()
        if not line or line.startswith("#"):
            continue
        _, _, value = line.partition("=")
        for path in value.split(","):
            path = path.strip()
            if path:
                paths.append(path)
    return paths


def main() -> int:
    parser = argparse.ArgumentParser()
    mode = parser.add_mutually_exclusive_group()
    mode.add_argument("--check", action="store_true",
                      help="exit non-zero if any sidecar-supplied file is present; change nothing")
    mode.add_argument("--dry-run", action="store_true", help="list what would be deleted")
    args = parser.parse_args()

    paths = sidecar_paths()
    present = [p for p in paths if (RESOURCES / p).is_file()]

    if args.check:
        if present:
            print(f"FAIL: {len(present)} file(s) the sidecar is supposed to supply are in the tree.")
            print("These are the original mod's art and must not be committed or shipped:")
            for path in present[:40]:
                print(f"  {path}")
            if len(present) > 40:
                print(f"  ... and {len(present) - 40} more")
            print("\nRun: python tools/strip_sidecar_assets.py")
            return 1
        print(f"OK: none of the {len(paths)} sidecar-supplied files are in the tree.")
        return 0

    if not present:
        print(f"nothing to do -- none of the {len(paths)} sidecar-supplied files are present.")
        return 0

    if args.dry_run:
        for path in present:
            print(f"would delete {path}")
        print(f"\n{len(present)} file(s)")
        return 0

    touched: set[Path] = set()
    for path in present:
        target = RESOURCES / path
        target.unlink()
        touched.add(target.parent)

    # Prune directories the deletion emptied, innermost first so a chain of them goes in
    # one pass rather than leaving the parent behind.
    removed_dirs = 0
    for directory in sorted(touched, key=lambda d: len(d.parts), reverse=True):
        current = directory
        while current != RESOURCES and current.is_dir() and not any(current.iterdir()):
            current.rmdir()
            removed_dirs += 1
            current = current.parent

    print(f"deleted {len(present)} file(s)"
          + (f" and {removed_dirs} empty director{'y' if removed_dirs == 1 else 'ies'}" if removed_dirs else ""))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
