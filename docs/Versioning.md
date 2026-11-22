# Versioning system
Auzonix uses a custom versioning system inspired by Semantic Versioning (SemVer).

Each version consists of two identifiers:
**1. Public Release (**`**MAJOR.MINOR**`**)**
Represents the public release of Auzonix.
- `MAJOR` — a major generation of the project, potentially introducing breaking changes.
- `MINOR` — a new public release introducing significant features and improvements.

**2. Auzonix Update ID (**`**YYmMMvN.TYPE**`**)**
Identifies a specific update within a public release.
- `YY` — last two digits of the year.
- `mMM` — release month.
- `vN` — sequential update number within the month.
- `.b` — major update.
- `.f` — feature update.
- `.p` — minor update or patch.
### Example
`1.2-26m11v5.b`
- `1.2` — public release.
- `26m11v5.b` — fifth major update of November 2026.

This system separates public release tracking from internal update tracking while preserving a recognizable versioning structure.

# Current Version
**`0.2-26m11v6.b`**
- `0.2` — current experimental public release (MVP 2.0).
- `26m11v6.b` — sixth major update of November 2026.

**Release Status:** Experimental / Alpha

Auzonix is currently an experimental project focused on testing its core concept and exploring its possibilities. It is not yet a stable production-ready solution. Bugs, unexpected behavior, and missing features are expected at this stage.

For more information about current restrictions and known issues, see  [**System Limitations**](SystemLimitations.md)